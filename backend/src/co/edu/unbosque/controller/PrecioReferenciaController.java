package co.edu.unbosque.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import co.edu.unbosque.dao.ImpresionDAO;
import co.edu.unbosque.dao.PrecioReferenciaDAO;
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

	public PrecioReferenciaController() {
		precioReferenciaDAO = new PrecioReferenciaDAOImpl();
		impresionDAO = new ImpresionDAOImpl();
	}

	/** Si no se indica la fecha, se toma la de hoy. Retorna el precio guardado, o null si no se pudo. */
	public PrecioReferenciaDTO registrarPrecio(PrecioReferenciaDTO original) {
		if (validarPrecio(original) != null) {
			return null;
		}
		PrecioReferenciaDTO dto = conFecha(original);
		if (!precioReferenciaDAO.registrarPrecio(PrecioReferenciaMapper.toEntity(dto))) {
			return null;
		}
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
		PrecioReferenciaDTO dto = conFecha(original);
		if (dto.valor() == null || dto.valor().signum() <= 0 || dto.valor().compareTo(VALOR_MAXIMO) > 0) {
			return "El valor es obligatorio y debe ser mayor que cero";
		}
		if (dto.valor().stripTrailingZeros().scale() > 2) {
			return "El valor admite máximo 2 decimales";
		}
		if (dto.fecha().isAfter(FechaUtil.hoy())) {
			return "La fecha de la cotización no puede ser futura";
		}
		if (dto.fuenteCotizacion() != null && dto.fuenteCotizacion().length() > 100) {
			return "La fuente admite máximo 100 caracteres";
		}
		if (impresionDAO.buscarImpresion(dto.idCarta(), dto.numeroImpresion()) == null) {
			return "No existe la impresión " + dto.idCarta() + "/" + dto.numeroImpresion();
		}
		if (precioReferenciaDAO.buscarPrecio(dto.idCarta(), dto.numeroImpresion(), dto.fecha()) != null) {
			return "Esa impresión ya tiene precio el " + dto.fecha() + ": solo se admite uno por día";
		}
		return null;
	}

	private PrecioReferenciaDTO conFecha(PrecioReferenciaDTO dto) {
		LocalDate fecha = dto.fecha() == null ? FechaUtil.hoy() : dto.fecha();
		return new PrecioReferenciaDTO(dto.idCarta(), dto.numeroImpresion(), fecha, dto.valor(), dto.fuenteCotizacion());
	}

}
