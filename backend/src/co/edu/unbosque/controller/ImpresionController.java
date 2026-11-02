package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.CartaDAO;
import co.edu.unbosque.dao.ImpresionDAO;
import co.edu.unbosque.dao.impl.CartaDAOImpl;
import co.edu.unbosque.dao.impl.ImpresionDAOImpl;
import co.edu.unbosque.dto.ImpresionDTO;
import co.edu.unbosque.mapper.ImpresionMapper;
import co.edu.unbosque.model.Impresion;

public class ImpresionController {

	private ImpresionDAO impresionDAO;
	private CartaDAO cartaDAO;

	public ImpresionController() {
		impresionDAO = new ImpresionDAOImpl();
		cartaDAO = new CartaDAOImpl();
	}

	/** Retorna la impresión creada con el número que se le asignó, o null si no se pudo. */
	public ImpresionDTO agregarImpresion(ImpresionDTO dto) {
		if (validarImpresion(dto, null) != null) {
			return null;
		}
		Impresion impresion = ImpresionMapper.toEntity(limpiar(dto));
		if (!impresionDAO.crearImpresion(impresion)) {
			return null;
		}
		return ImpresionMapper.toDTO(impresion);
	}

	public boolean actualizarImpresion(ImpresionDTO dto, int idCarta, int numeroImpresion) {
		ImpresionDTO conLlave = conLlave(dto, idCarta, numeroImpresion);
		if (validarImpresion(conLlave, numeroImpresion) != null) {
			return false;
		}
		return impresionDAO.actualizarImpresion(ImpresionMapper.toEntity(limpiar(conLlave)), idCarta, numeroImpresion);
	}

	public boolean eliminarImpresion(int idCarta, int numeroImpresion) {
		return impresionDAO.eliminarImpresion(idCarta, numeroImpresion);
	}

	public List<ImpresionDTO> mostrarImpresiones() {
		return impresionDAO.mostrarImpresiones().stream().map(ImpresionMapper::toDTO).toList();
	}

	public List<ImpresionDTO> mostrarImpresionesDeCarta(int idCarta) {
		return impresionDAO.mostrarImpresionesDeCarta(idCarta).stream().map(ImpresionMapper::toDTO).toList();
	}

	public List<ImpresionDTO> mostrarReimpresiones(int idCarta, int numeroImpresion) {
		return impresionDAO.mostrarReimpresiones(idCarta, numeroImpresion).stream().map(ImpresionMapper::toDTO)
				.toList();
	}

	public ImpresionDTO buscarImpresion(int idCarta, int numeroImpresion) {
		Impresion impresion = impresionDAO.buscarImpresion(idCarta, numeroImpresion);
		if (impresion == null) {
			return null;
		}
		return ImpresionMapper.toDTO(impresion);
	}

	/**
	 * Revisa los datos y las reglas del linaje de reimpresiones (sección C del
	 * documento de supuestos). numeroActual es null al crear y el número de la
	 * impresión al actualizar. Retorna null si todo está bien, o el motivo.
	 */
	public String validarImpresion(ImpresionDTO original, Integer numeroActual) {
		if (original == null) {
			return "No llegaron los datos de la impresión";
		}
		ImpresionDTO dto = limpiar(original);
		if (dto.numeroColeccion() == null || dto.numeroColeccion().isBlank()) {
			return "El número de colección es obligatorio";
		}
		if (dto.numeroColeccion().length() > 10) {
			return "El número de colección admite máximo 10 caracteres";
		}
		if (dto.idCarta() <= 0 || dto.idEdicion() <= 0 || dto.idIdioma() <= 0 || dto.idRareza() <= 0
				|| dto.idArtista() <= 0) {
			return "idCarta, idEdicion, idIdioma, idRareza e idArtista son obligatorios";
		}
		if (numeroActual == null && cartaDAO.buscarCarta(dto.idCarta()) == null) {
			return "No existe la carta " + dto.idCarta();
		}

		Impresion fisica = impresionDAO.buscarImpresionFisica(dto.idEdicion(), dto.idIdioma(), dto.numeroColeccion(),
				dto.foil());
		if (fisica != null && !(fisica.getIdCarta() == dto.idCarta() && Integer.valueOf(fisica.getNumeroImpresion()).equals(numeroActual))) {
			return "Ya existe una impresión con esa edición, idioma, número de colección y acabado";
		}
		if (dto.originalNumeroImpresion() == null) {
			return null;
		}
		if (dto.originalNumeroImpresion().equals(numeroActual)) {
			return "Una impresión no puede ser reimpresión de sí misma";
		}
		Impresion impresionOriginal = impresionDAO.buscarImpresion(dto.idCarta(), dto.originalNumeroImpresion());
		if (impresionOriginal == null) {
			return "La impresión original " + dto.originalNumeroImpresion() + " no existe para esta carta";
		}
		// El linaje se resuelve en un solo paso: toda reimpresión apunta a la original.
		if (!impresionOriginal.esPrimeraEdicion()) {
			return "La impresión " + dto.originalNumeroImpresion() + " ya es una reimpresión; apunte a su original, la "
					+ impresionOriginal.getOriginalNumeroImpresion();
		}
		if (numeroActual != null && !impresionDAO.mostrarReimpresiones(dto.idCarta(), numeroActual).isEmpty()) {
			return "Esta impresión tiene reimpresiones registradas: no puede volverse reimpresión de otra";
		}
		return null;
	}

	private ImpresionDTO conLlave(ImpresionDTO dto, int idCarta, int numeroImpresion) {
		if (dto == null) {
			return null;
		}
		return new ImpresionDTO(idCarta, numeroImpresion, dto.numeroColeccion(), dto.foil(), dto.idEdicion(),
				dto.idIdioma(), dto.idRareza(), dto.idArtista(), dto.originalNumeroImpresion());
	}

	private ImpresionDTO limpiar(ImpresionDTO dto) {
		String numeroColeccion = dto.numeroColeccion() == null ? null : dto.numeroColeccion().trim();
		return new ImpresionDTO(dto.idCarta(), dto.numeroImpresion(), numeroColeccion, dto.foil(), dto.idEdicion(),
				dto.idIdioma(), dto.idRareza(), dto.idArtista(), dto.originalNumeroImpresion());
	}

}
