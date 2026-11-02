package co.edu.unbosque.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.dao.ReservaDAO;
import co.edu.unbosque.dao.impl.EjemplarDAOImpl;
import co.edu.unbosque.dao.impl.ReservaDAOImpl;
import co.edu.unbosque.dto.ApartadoDTO;
import co.edu.unbosque.dto.ReservaDTO;
import co.edu.unbosque.mapper.ReservaMapper;
import co.edu.unbosque.model.Ejemplar;
import co.edu.unbosque.model.Reserva;
import co.edu.unbosque.util.Configuracion;
import co.edu.unbosque.util.FechaUtil;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;

/**
 * Las reservas vencidas se liberan cada vez que alguien consulta u opera: no hace
 * falta intervención del personal ni un proceso programado (sección H).
 */
public class ReservaController {

	public static final String ACTIVA = "ACTIVA";

	private ReservaDAO reservaDAO;
	private EjemplarDAO ejemplarDAO;

	public ReservaController() {
		reservaDAO = new ReservaDAOImpl();
		ejemplarDAO = new EjemplarDAOImpl();
	}

	/** Retorna la reserva creada, o null si no se pudo. La crea el cliente de la sesión. */
	public ReservaDTO crearReserva(List<Integer> ejemplares, SesionActiva cliente) {
		if (validarReserva(ejemplares) != null) {
			return null;
		}
		LocalDate hoy = FechaUtil.hoy();
		int dias = Integer.parseInt(Configuracion.obtener("reserva.dias", "30"));
		Reserva reserva = new Reserva(0, hoy, ACTIVA, hoy.plusDays(dias), cliente.tipoDocumento(),
				cliente.numeroDocumento(), null, sinRepetidos(ejemplares));
		if (!reservaDAO.crearReserva(reserva)) {
			return null;
		}
		return ReservaMapper.toDTO(reserva);
	}

	/** Retorna null si los ejemplares se pueden apartar, o el motivo del rechazo. */
	public String validarReserva(List<Integer> ejemplares) {
		if (ejemplares == null || ejemplares.isEmpty()) {
			return "Indique al menos un ejemplar para apartar";
		}
		reservaDAO.vencerReservas(FechaUtil.hoy());
		for (int idEjemplar : sinRepetidos(ejemplares)) {
			Ejemplar ejemplar = ejemplarDAO.buscarEjemplar(idEjemplar);
			if (ejemplar == null) {
				return "No existe el ejemplar " + idEjemplar;
			}
			if (!EjemplarController.DISPONIBLE.equals(ejemplar.getEstado())) {
				return "El ejemplar " + idEjemplar + " no está disponible";
			}
		}
		return null;
	}

	/** Un cliente solo cancela sus propias reservas. Retorna null si se hizo, o el motivo. */
	public String cancelarReserva(int consecutivo, SesionActiva cliente) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		Reserva reserva = reservaDAO.buscarReserva(consecutivo);
		if (reserva == null || !esDe(reserva, cliente)) {
			return "No existe la reserva " + consecutivo;
		}
		if (!ACTIVA.equals(reserva.getEstado())) {
			return "Solo se puede cancelar una reserva ACTIVA; esta está " + reserva.getEstado();
		}
		return reservaDAO.cancelarReserva(consecutivo) ? null : "No se pudo cancelar la reserva";
	}

	/** El cliente solo ve las suyas; el personal ve todas y puede filtrar por estado. */
	public List<ReservaDTO> mostrarReservas(SesionActiva sesion, String estado) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		List<Reserva> reservas = sesion.esCliente()
				? reservaDAO.mostrarReservasDeCliente(sesion.tipoDocumento(), sesion.numeroDocumento())
				: reservaDAO.mostrarReservas(estado);
		return reservas.stream().map(ReservaMapper::toDTO).toList();
	}

	/** Un cliente no puede saber si existe una reserva ajena: para él, no existe. */
	public ReservaDTO buscarReserva(int consecutivo, SesionActiva sesion) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		Reserva reserva = reservaDAO.buscarReserva(consecutivo);
		if (reserva == null || (sesion.esCliente() && !esDe(reserva, sesion))) {
			return null;
		}
		return ReservaMapper.toDTO(reserva);
	}

	public List<ApartadoDTO> mostrarApartados(int consecutivo) {
		return reservaDAO.mostrarApartados(consecutivo);
	}

	private boolean esDe(Reserva reserva, SesionActiva cliente) {
		return reserva.getClienteTipoDocumento().equals(cliente.tipoDocumento())
				&& reserva.getClienteNumeroDocumento().equals(cliente.numeroDocumento());
	}

	private List<Integer> sinRepetidos(List<Integer> ejemplares) {
		return new ArrayList<>(new LinkedHashSet<>(ejemplares));
	}

}
