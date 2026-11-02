package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.IdiomaDAO;
import co.edu.unbosque.dao.impl.IdiomaDAOImpl;
import co.edu.unbosque.dto.IdiomaDTO;
import co.edu.unbosque.mapper.IdiomaMapper;
import co.edu.unbosque.model.Idioma;

public class IdiomaController {

	private IdiomaDAO idiomaDAO;

	public IdiomaController() {
		idiomaDAO = new IdiomaDAOImpl();
	}

	/** Retorna el idioma creado con el id que asignó MySQL, o null si no se pudo. */
	public IdiomaDTO agregarIdioma(IdiomaDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Idioma idioma = IdiomaMapper.toEntity(dto);
		if (!idiomaDAO.crearIdioma(idioma)) {
			return null;
		}
		return IdiomaMapper.toDTO(idioma);
	}

	public boolean actualizarIdioma(IdiomaDTO dto, int idIdioma) {
		if (!esValido(dto)) {
			return false;
		}
		return idiomaDAO.actualizarIdioma(IdiomaMapper.toEntity(dto), idIdioma);
	}

	public List<IdiomaDTO> mostrarIdiomas() {
		return idiomaDAO.mostrarIdiomas().stream().map(IdiomaMapper::toDTO).toList();
	}

	public IdiomaDTO buscarIdioma(int idIdioma) {
		Idioma idioma = idiomaDAO.buscarIdioma(idIdioma);
		if (idioma == null) {
			return null;
		}
		return IdiomaMapper.toDTO(idioma);
	}

	/** Código y nombre obligatorios. */
	private boolean esValido(IdiomaDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.codigo() == null || dto.codigo().isBlank() || dto.codigo().length() > 10
				|| dto.nombre() == null || dto.nombre().isBlank() || dto.nombre().length() > 50);
	}

}
