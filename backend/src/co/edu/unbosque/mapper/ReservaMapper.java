package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.ReservaDTO;
import co.edu.unbosque.model.Reserva;

public class ReservaMapper {

	public static ReservaDTO toDTO(Reserva entidad) {
		return new ReservaDTO(entidad.getConsecutivo(), entidad.getFechaCreacion(), entidad.getEstado(),
				entidad.getFechaVencimiento(), entidad.getClienteTipoDocumento(), entidad.getClienteNumeroDocumento(),
				entidad.getConsecutivoSalida(), entidad.getEjemplares());
	}

	public static Reserva toEntity(ReservaDTO dto) {
		return new Reserva(dto.consecutivo(), dto.fechaCreacion(), dto.estado(), dto.fechaVencimiento(),
				dto.clienteTipoDocumento(), dto.clienteNumeroDocumento(), dto.consecutivoSalida(), dto.ejemplares());
	}

}
