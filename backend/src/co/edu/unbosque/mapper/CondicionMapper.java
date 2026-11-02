package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.CondicionDTO;
import co.edu.unbosque.model.Condicion;

public class CondicionMapper {

	public static CondicionDTO toDTO(Condicion entidad) {
		return new CondicionDTO(entidad.getCodigoCondicion(), entidad.getNombre(), entidad.getFactorAjuste());
	}

	public static Condicion toEntity(CondicionDTO dto) {
		return new Condicion(dto.codigoCondicion(), dto.nombre(), dto.factorAjuste());
	}

}
