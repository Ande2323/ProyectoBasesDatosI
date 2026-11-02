package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.RolDTO;
import co.edu.unbosque.model.Rol;

public class RolMapper {

	public static RolDTO toDTO(Rol entidad) {
		return new RolDTO(entidad.getCodigoRol(), entidad.getNombre());
	}

	public static Rol toEntity(RolDTO dto) {
		return new Rol(dto.codigoRol(), dto.nombre());
	}

}
