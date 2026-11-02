package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.IdiomaDTO;
import co.edu.unbosque.model.Idioma;

public class IdiomaMapper {

	public static IdiomaDTO toDTO(Idioma entidad) {
		return new IdiomaDTO(entidad.getIdIdioma(), entidad.getCodigo(), entidad.getNombre());
	}

	public static Idioma toEntity(IdiomaDTO dto) {
		return new Idioma(dto.idIdioma(), dto.codigo(), dto.nombre());
	}

}
