package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.SalidaDTO;
import co.edu.unbosque.model.Salida;

public class SalidaMapper {

	public static SalidaDTO toDTO(Salida entidad) {
		return new SalidaDTO(entidad.getConsecutivoSalida(), entidad.getMotivo(), entidad.getObservaciones(),
				entidad.getFecha(), entidad.getClienteTipoDocumento(), entidad.getClienteNumeroDocumento(),
				entidad.getEmpleadoTipoDocumento(), entidad.getEmpleadoNumeroDocumento(), entidad.getEjemplares(),
				entidad.getConsecutivoReserva());
	}

	public static Salida toEntity(SalidaDTO dto) {
		return new Salida(dto.consecutivoSalida(), dto.motivo(), dto.observaciones(), dto.fecha(),
				dto.clienteTipoDocumento(), dto.clienteNumeroDocumento(), dto.empleadoTipoDocumento(),
				dto.empleadoNumeroDocumento(), dto.ejemplares(), dto.consecutivoReserva());
	}

}
