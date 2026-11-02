package co.edu.unbosque.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import co.edu.unbosque.dao.CondicionDAO;
import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.dao.ImpresionDAO;
import co.edu.unbosque.dao.PrecioReferenciaDAO;
import co.edu.unbosque.dao.ReservaDAO;
import co.edu.unbosque.dao.impl.CondicionDAOImpl;
import co.edu.unbosque.dao.impl.EjemplarDAOImpl;
import co.edu.unbosque.dao.impl.ImpresionDAOImpl;
import co.edu.unbosque.dao.impl.PrecioReferenciaDAOImpl;
import co.edu.unbosque.dao.impl.ReservaDAOImpl;
import co.edu.unbosque.dto.EjemplarDTO;
import co.edu.unbosque.mapper.EjemplarMapper;
import co.edu.unbosque.model.Condicion;
import co.edu.unbosque.model.Ejemplar;
import co.edu.unbosque.model.PrecioReferencia;
import co.edu.unbosque.util.FechaUtil;

public class EjemplarController {

	public static final String DISPONIBLE = "DISPONIBLE";
	private static final List<String> ESTADOS = List.of(DISPONIBLE, "RESERVADO", "VENDIDO", "BAJA");

	private EjemplarDAO ejemplarDAO;
	private ImpresionDAO impresionDAO;
	private CondicionDAO condicionDAO;
	private PrecioReferenciaDAO precioReferenciaDAO;
	private ReservaDAO reservaDAO;

	public EjemplarController() {
		ejemplarDAO = new EjemplarDAOImpl();
		impresionDAO = new ImpresionDAOImpl();
		condicionDAO = new CondicionDAOImpl();
		precioReferenciaDAO = new PrecioReferenciaDAOImpl();
		reservaDAO = new ReservaDAOImpl();
	}

	/**
	 * Registra el ingreso. El estado inicial es DISPONIBLE y el precio sugerido
	 * se fija aquí, una sola vez: última cotización de la impresión por el factor
	 * de la condición. Si la impresión aún no tiene precio, queda vacío.
	 */
	public EjemplarDTO registrarIngreso(EjemplarDTO original) {
		if (validarIngreso(original) != null) {
			return null;
		}
		EjemplarDTO dto = limpiar(original);
		LocalDate fecha = dto.fechaIngreso() == null ? FechaUtil.hoy() : dto.fechaIngreso();
		Ejemplar ejemplar = new Ejemplar(0, dto.codigoEtiqueta(), dto.ubicacion(), DISPONIBLE,
				calcularPrecioSugerido(dto.idCarta(), dto.numeroImpresion(), dto.codigoCondicion()), fecha,
				dto.observacionesDanos(), dto.idCarta(), dto.numeroImpresion(), dto.codigoCondicion(), null);
		if (!ejemplarDAO.crearEjemplar(ejemplar)) {
			return null;
		}
		return EjemplarMapper.toDTO(ejemplar);
	}

	public boolean actualizarEjemplar(EjemplarDTO dto, int idEjemplar) {
		if (validarActualizacion(dto) != null) {
			return false;
		}
		return ejemplarDAO.actualizarEjemplar(EjemplarMapper.toEntity(limpiar(dto)), idEjemplar);
	}

	/** Retorna null si se hizo, o el motivo. Daños y pérdidas no generan salida (sección D). */
	public String darDeBajaEjemplar(int idEjemplar, String observaciones) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		Ejemplar ejemplar = ejemplarDAO.buscarEjemplar(idEjemplar);
		if (ejemplar == null) {
			return "No existe el ejemplar " + idEjemplar;
		}
		if (!DISPONIBLE.equals(ejemplar.getEstado())) {
			return "Solo se puede dar de baja un ejemplar DISPONIBLE; este está " + ejemplar.getEstado();
		}
		if (!ejemplarDAO.darDeBajaEjemplar(idEjemplar, observaciones)) {
			return "No se pudo dar de baja el ejemplar";
		}
		return null;
	}

	/** Las reservas vencidas se liberan antes de consultar: así la disponibilidad siempre es real. */
	public List<EjemplarDTO> mostrarEjemplares(String estado, Integer idCarta, Integer numeroImpresion) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		return ejemplarDAO.mostrarEjemplares(estado, idCarta, numeroImpresion).stream().map(EjemplarMapper::toDTO)
				.toList();
	}

	public EjemplarDTO buscarEjemplar(int idEjemplar) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		Ejemplar ejemplar = ejemplarDAO.buscarEjemplar(idEjemplar);
		return ejemplar == null ? null : EjemplarMapper.toDTO(ejemplar);
	}

	public boolean esEstadoValido(String estado) {
		return ESTADOS.contains(estado);
	}

	/** Retorna null si los datos sirven, o el motivo del rechazo. */
	public String validarIngreso(EjemplarDTO original) {
		if (original == null) {
			return "No llegaron los datos del ejemplar";
		}
		EjemplarDTO dto = limpiar(original);
		if (dto.codigoEtiqueta() == null || dto.codigoEtiqueta().isBlank() || dto.codigoEtiqueta().length() > 30) {
			return "El código de etiqueta es obligatorio (máximo 30 caracteres)";
		}
		String motivo = validarActualizacion(dto);
		if (motivo != null) {
			return motivo;
		}
		if (dto.fechaIngreso() != null && dto.fechaIngreso().isAfter(FechaUtil.hoy())) {
			return "La fecha de ingreso no puede ser futura";
		}
		if (impresionDAO.buscarImpresion(dto.idCarta(), dto.numeroImpresion()) == null) {
			return "No existe la impresión " + dto.idCarta() + "/" + dto.numeroImpresion();
		}
		if (ejemplarDAO.buscarEjemplarPorEtiqueta(dto.codigoEtiqueta()) != null) {
			return "Ya hay un ejemplar con la etiqueta " + dto.codigoEtiqueta();
		}
		return null;
	}

	/** Ubicación, condición y observaciones: lo único que el personal puede cambiar. */
	public String validarActualizacion(EjemplarDTO original) {
		if (original == null) {
			return "No llegaron los datos del ejemplar";
		}
		EjemplarDTO dto = limpiar(original);
		if (dto.ubicacion() != null && dto.ubicacion().length() > 50) {
			return "La ubicación admite máximo 50 caracteres";
		}
		if (dto.observacionesDanos() != null && dto.observacionesDanos().length() > 500) {
			return "Las observaciones admiten máximo 500 caracteres";
		}
		if (dto.codigoCondicion() == null || condicionDAO.buscarCondicion(dto.codigoCondicion()) == null) {
			return "La condición es obligatoria y debe existir (NM, LP, MP, HP, DMG)";
		}
		return null;
	}

	private BigDecimal calcularPrecioSugerido(int idCarta, int numeroImpresion, String codigoCondicion) {
		PrecioReferencia vigente = precioReferenciaDAO.buscarPrecioVigente(idCarta, numeroImpresion);
		Condicion condicion = condicionDAO.buscarCondicion(codigoCondicion);
		if (vigente == null || condicion == null) {
			return null;
		}
		return vigente.getValor().multiply(BigDecimal.valueOf(condicion.getFactorAjuste())).setScale(2,
				RoundingMode.HALF_UP);
	}

	private EjemplarDTO limpiar(EjemplarDTO dto) {
		String etiqueta = dto.codigoEtiqueta() == null ? null : dto.codigoEtiqueta().trim().toUpperCase();
		String condicion = dto.codigoCondicion() == null ? null : dto.codigoCondicion().trim().toUpperCase();
		String ubicacion = dto.ubicacion() == null ? null : dto.ubicacion().trim();
		return new EjemplarDTO(dto.idEjemplar(), etiqueta, ubicacion, dto.estado(), dto.precioSugerido(),
				dto.fechaIngreso(), dto.observacionesDanos(), dto.idCarta(), dto.numeroImpresion(), condicion,
				dto.consecutivoSalida());
	}

}
