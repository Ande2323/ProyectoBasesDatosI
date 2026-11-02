package co.edu.unbosque.controller;

import java.math.BigDecimal;
import java.util.List;

import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.dao.ImpresionDAO;
import co.edu.unbosque.dao.PrecioReferenciaDAO;
import co.edu.unbosque.dao.impl.EjemplarDAOImpl;
import co.edu.unbosque.dao.impl.ImpresionDAOImpl;
import co.edu.unbosque.dao.impl.PrecioReferenciaDAOImpl;
import co.edu.unbosque.dto.PrecioReferenciaDTO;
import co.edu.unbosque.mapper.PrecioReferenciaMapper;
import co.edu.unbosque.model.PrecioReferencia;
import co.edu.unbosque.util.FechaUtil;

public class PrecioReferenciaController {

	private static final BigDecimal VALOR_MAXIMO = new BigDecimal("9999999999.99");

	private PrecioReferenciaDAO precioReferenciaDAO;
	private ImpresionDAO impresionDAO;
	private EjemplarDAO ejemplarDAO;

	public PrecioReferenciaController() {
		precioReferenciaDAO = new PrecioReferenciaDAOImpl();
		impresionDAO = new ImpresionDAOImpl();
		ejemplarDAO = new EjemplarDAOImpl();
	}

	/**
	 * El precio de una impresión cambia máximo una vez al día: la fecha siempre es la de
	 * hoy, y nadie la elige. Las copias de la impresión toman el precio nuevo.
	 * Retorna el precio guardado, o null si no se pudo.
	 */
	public PrecioReferenciaDTO registrarPrecio(PrecioReferenciaDTO original) {
		if (validarPrecio(original) != null) {
			return null;
		}
		PrecioReferenciaDTO dto = conFechaDeHoy(original);
		if (!precioReferenciaDAO.registrarPrecio(PrecioReferenciaMapper.toEntity(dto))) {
			return null;
		}
		ejemplarDAO.recalcularPreciosDeImpresion(dto.idCarta(), dto.numeroImpresion());
		return dto;
	}

	public List<PrecioReferenciaDTO> mostrarHistorial(int idCarta, int numeroImpresion) {
		return precioReferenciaDAO.mostrarHistorial(idCarta, numeroImpresion).stream()
				.map(PrecioReferenciaMapper::toDTO).toList();
	}

	public PrecioReferenciaDTO buscarPrecioVigente(int idCarta, int numeroImpresion) {
		PrecioReferencia precio = precioReferenciaDAO.buscarPrecioVigente(idCarta, numeroImpresion);
		return precio == null ? null : PrecioReferenciaMapper.toDTO(precio);
	}

	/** Retorna null si los datos sirven, o el motivo del rechazo. */
	public String validarPrecio(PrecioReferenciaDTO original) {
		if (original == null) {
			return "No llegaron los datos del precio";
		}
		PrecioReferenciaDTO dto = conFechaDeHoy(original);
		if (dto.valor() == null || dto.valor().signum() <= 0 || dto.valor().compareTo(VALOR_MAXIMO) > 0) {
			return "El valor es obligatorio y debe ser mayor que cero";
		}
		if (dto.valor().stripTrailingZeros().scale() > 2) {
			return "El valor admite máximo 2 decimales";
		}
		if (dto.fuenteCotizacion() != null && dto.fuenteCotizacion().length() > 100) {
			return "La fuente admite máximo 100 caracteres";
		}
		if (impresionDAO.buscarImpresion(dto.idCarta(), dto.numeroImpresion()) == null) {
			return "No existe la impresión " + dto.idCarta() + "/" + dto.numeroImpresion();
		}
		if (precioReferenciaDAO.buscarPrecio(dto.idCarta(), dto.numeroImpresion(), dto.fecha()) != null) {
			return "El precio de esta impresión ya se cambió hoy: se puede volver a cambiar mañana";
		}
		return null;
	}

	private PrecioReferenciaDTO conFechaDeHoy(PrecioReferenciaDTO dto) {
		return new PrecioReferenciaDTO(dto.idCarta(), dto.numeroImpresion(), FechaUtil.hoy(), dto.valor(), dto.fuenteCotizacion());
	}

}
