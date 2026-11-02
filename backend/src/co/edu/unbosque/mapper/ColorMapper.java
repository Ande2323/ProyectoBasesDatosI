package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.ColorDTO;
import co.edu.unbosque.model.Color;

public class ColorMapper {

	public static ColorDTO toDTO(Color entidad) {
		return new ColorDTO(entidad.getIdColor(), entidad.getNombreColor(), entidad.getOrdenCanonico());
	}

	public static Color toEntity(ColorDTO dto) {
		return new Color(dto.idColor(), dto.nombreColor(), dto.ordenCanonico());
	}

}
