package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.EmpleadoDTO;
import co.edu.unbosque.model.Empleado;

public class EmpleadoMapper {

	public static EmpleadoDTO toDTO(Empleado entidad) {
		return new EmpleadoDTO(entidad.getTipoDocumento(), entidad.getNumeroDocumento(), entidad.getNombres(),
				entidad.getApellidos(), entidad.getCargo(), entidad.getEstado(), entidad.getFechaVinculacion(),
				entidad.getIdCuentaU());
	}

	public static Empleado toEntity(EmpleadoDTO dto) {
		return new Empleado(dto.tipoDocumento(), dto.numeroDocumento(), dto.nombres(), dto.apellidos(), dto.cargo(),
				dto.estado(), dto.fechaVinculacion(), dto.idCuentaU());
	}

}
