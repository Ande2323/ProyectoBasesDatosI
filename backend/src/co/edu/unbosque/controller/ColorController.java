package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.ColorDAO;
import co.edu.unbosque.dao.impl.ColorDAOImpl;
import co.edu.unbosque.dto.ColorDTO;
import co.edu.unbosque.mapper.ColorMapper;
import co.edu.unbosque.model.Color;

public class ColorController {

	private ColorDAO colorDAO;

	public ColorController() {
		colorDAO = new ColorDAOImpl();
	}

	/** Retorna el color creado con el id que asignó MySQL, o null si no se pudo. */
	public ColorDTO agregarColor(ColorDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Color color = ColorMapper.toEntity(dto);
		if (!colorDAO.crearColor(color)) {
			return null;
		}
		return ColorMapper.toDTO(color);
	}

	public boolean actualizarColor(ColorDTO dto, int idColor) {
		if (!esValido(dto)) {
			return false;
		}
		return colorDAO.actualizarColor(ColorMapper.toEntity(dto), idColor);
	}

	public boolean eliminarColor(int idColor) {
		return colorDAO.eliminarColor(idColor);
	}

	public List<ColorDTO> mostrarColores() {
		return colorDAO.mostrarColores().stream().map(ColorMapper::toDTO).toList();
	}

	public ColorDTO buscarColor(int idColor) {
		Color color = colorDAO.buscarColor(idColor);
		if (color == null) {
			return null;
		}
		return ColorMapper.toDTO(color);
	}

	/** El nombre es obligatorio y el orden canónico, si se da, va de 1 a 99. */
	private boolean esValido(ColorDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.nombreColor() == null || dto.nombreColor().isBlank() || dto.nombreColor().length() > 30
				|| dto.ordenCanonico() != null && (dto.ordenCanonico() <= 0 || dto.ordenCanonico() > 99));
	}

}
