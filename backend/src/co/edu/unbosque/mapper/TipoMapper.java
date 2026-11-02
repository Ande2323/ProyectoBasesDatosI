package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.TipoDTO;
import co.edu.unbosque.model.Tipo;

public class TipoMapper {

	public static TipoDTO toDTO(Tipo entidad) {
		return new TipoDTO(entidad.getIdTipo(), entidad.getNivel(), entidad.getCodigo(), entidad.getNombre());
	}

	public static Tipo toEntity(TipoDTO dto) {
		return new Tipo(dto.idTipo(), dto.nivel(), dto.codigo(), dto.nombre());
	}

}
