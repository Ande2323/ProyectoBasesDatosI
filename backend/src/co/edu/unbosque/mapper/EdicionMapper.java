package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.EdicionDTO;
import co.edu.unbosque.model.Edicion;

public class EdicionMapper {

	public static EdicionDTO toDTO(Edicion entidad) {
		return new EdicionDTO(entidad.getIdEdicion(), entidad.getCodigoImpreso(), entidad.getNombre(), entidad.getFechaLanzamiento(), entidad.getCantidadCartas(), entidad.getTipoEdicion());
	}

	public static Edicion toEntity(EdicionDTO dto) {
		return new Edicion(dto.idEdicion(), dto.codigoImpreso(), dto.nombre(), dto.fechaLanzamiento(), dto.cantidadCartas(), dto.tipoEdicion());
	}

}
