package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.RarezaDTO;
import co.edu.unbosque.model.Rareza;

public class RarezaMapper {

	public static RarezaDTO toDTO(Rareza entidad) {
		return new RarezaDTO(entidad.getIdRareza(), entidad.getCodigo(), entidad.getNombre());
	}

	public static Rareza toEntity(RarezaDTO dto) {
		return new Rareza(dto.idRareza(), dto.codigo(), dto.nombre());
	}

}
