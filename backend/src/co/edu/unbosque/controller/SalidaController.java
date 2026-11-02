package co.edu.unbosque.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import co.edu.unbosque.dao.ClienteDAO;
import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.dao.ReservaDAO;
import co.edu.unbosque.dao.SalidaDAO;
import co.edu.unbosque.dao.impl.ClienteDAOImpl;
import co.edu.unbosque.dao.impl.EjemplarDAOImpl;
import co.edu.unbosque.dao.impl.ReservaDAOImpl;
import co.edu.unbosque.dao.impl.SalidaDAOImpl;
import co.edu.unbosque.dto.SalidaDTO;
import co.edu.unbosque.mapper.SalidaMapper;
import co.edu.unbosque.model.Ejemplar;
import co.edu.unbosque.model.Reserva;
import co.edu.unbosque.model.Salida;
import co.edu.unbosque.util.FechaUtil;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;

/**
 * Toda salida tiene cliente identificado y queda atribuida al empleado que la
 * registra: el de la sesión (sección I). No hay entregas anónimas.
 */
public class SalidaController {

	public static final String MOTIVO_DIRECTA = "VENTA";
	public static final String MOTIVO_RECLAMO = "ENTREGA DE RESERVA";

	private SalidaDAO salidaDAO;
	private ReservaDAO reservaDAO;
	private EjemplarDAO ejemplarDAO;
	private ClienteDAO clienteDAO;

	public SalidaController() {
		salidaDAO = new SalidaDAOImpl();
		reservaDAO = new ReservaDAOImpl();
		ejemplarDAO = new EjemplarDAOImpl();
		clienteDAO = new ClienteDAOImpl();
	}

	/** Entrega directa en mostrador. Retorna la salida, o null si no se pudo. */
	public SalidaDTO registrarEntregaDirecta(SalidaDTO dto, SesionActiva empleado) {
		if (validarEntregaDirecta(dto) != null) {
			return null;
		}
		Salida salida = new Salida(0, MOTIVO_DIRECTA, dto.observaciones(), FechaUtil.hoy(), dto.clienteTipoDocumento(),
				dto.clienteNumeroDocumento(), empleado.tipoDocumento(), empleado.numeroDocumento(),
				sinRepetidos(dto.ejemplares()), null);
		if (!salidaDAO.registrarEntregaDirecta(salida)) {
			return null;
		}
		return SalidaMapper.toDTO(salida);
	}

	/**
	 * Reclamo de una reserva. El cliente es siempre el de la reserva. Si no se
	 * indican ejemplares, se entregan todos; los que no se recojan vuelven a estar
	 * disponibles, porque una reserva origina una sola salida.
	 */
	public SalidaDTO registrarReclamo(SalidaDTO dto, SesionActiva empleado) {
		if (validarReclamo(dto) != null) {
			return null;
		}
		Reserva reserva = reservaDAO.buscarReserva(dto.consecutivoReserva());
		List<Integer> ejemplares = dto.ejemplares() == null || dto.ejemplares().isEmpty() ? reserva.getEjemplares()
				: sinRepetidos(dto.ejemplares());
		Salida salida = new Salida(0, MOTIVO_RECLAMO, dto.observaciones(), FechaUtil.hoy(),
				reserva.getClienteTipoDocumento(), reserva.getClienteNumeroDocumento(), empleado.tipoDocumento(),
				empleado.numeroDocumento(), ejemplares, reserva.getConsecutivo());
		if (!salidaDAO.registrarReclamo(salida)) {
			return null;
		}
		return SalidaMapper.toDTO(salida);
	}

	public List<SalidaDTO> mostrarSalidas() {
		return salidaDAO.mostrarSalidas().stream().map(SalidaMapper::toDTO).toList();
	}

	public SalidaDTO buscarSalida(int consecutivoSalida) {
		Salida salida = salidaDAO.buscarSalida(consecutivoSalida);
		return salida == null ? null : SalidaMapper.toDTO(salida);
	}

	/** Retorna null si la entrega se puede hacer, o el motivo del rechazo. */
	public String validarEntregaDirecta(SalidaDTO dto) {
		if (dto == null) {
			return "No llegaron los datos de la salida";
		}
		reservaDAO.vencerReservas(FechaUtil.hoy());
		if (dto.clienteTipoDocumento() == null || dto.clienteNumeroDocumento() == null
				|| clienteDAO.buscarCliente(dto.clienteTipoDocumento(), dto.clienteNumeroDocumento()) == null) {
			return "Toda salida debe tener un cliente registrado: revise el documento";
		}
		if (dto.ejemplares() == null || dto.ejemplares().isEmpty()) {
			return "Indique al menos un ejemplar para entregar";
		}
		String motivo = validarObservaciones(dto);
		if (motivo != null) {
			return motivo;
		}
		for (int idEjemplar : sinRepetidos(dto.ejemplares())) {
			Ejemplar ejemplar = ejemplarDAO.buscarEjemplar(idEjemplar);
			if (ejemplar == null) {
				return "No existe el ejemplar " + idEjemplar;
			}
			if (!EjemplarController.DISPONIBLE.equals(ejemplar.getEstado())) {
				return "El ejemplar " + idEjemplar + " no está disponible: está " + ejemplar.getEstado();
			}
		}
		return null;
	}

	/** Retorna null si la reserva se puede reclamar, o el motivo del rechazo. */
	public String validarReclamo(SalidaDTO dto) {
		if (dto == null || dto.consecutivoReserva() == null) {
			return "Indique la reserva que se reclama";
		}
		reservaDAO.vencerReservas(FechaUtil.hoy());
		Reserva reserva = reservaDAO.buscarReserva(dto.consecutivoReserva());
		if (reserva == null) {
			return "No existe la reserva " + dto.consecutivoReserva();
		}
		if (!ReservaController.ACTIVA.equals(reserva.getEstado())) {
			return "Solo se puede reclamar una reserva ACTIVA; esta está " + reserva.getEstado();
		}
		if (dto.ejemplares() != null && !reserva.getEjemplares().containsAll(dto.ejemplares())) {
			return "Solo se pueden entregar ejemplares que estén en la reserva " + reserva.getConsecutivo();
		}
		return validarObservaciones(dto);
	}

	private String validarObservaciones(SalidaDTO dto) {
		if (dto.observaciones() != null && dto.observaciones().length() > 500) {
			return "Las observaciones admiten máximo 500 caracteres";
		}
		return null;
	}

	private List<Integer> sinRepetidos(List<Integer> ejemplares) {
		return new ArrayList<>(new LinkedHashSet<>(ejemplares));
	}

}
