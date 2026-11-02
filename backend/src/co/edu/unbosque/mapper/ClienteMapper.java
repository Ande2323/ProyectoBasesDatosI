package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.ClienteDTO;
import co.edu.unbosque.model.Cliente;

public class ClienteMapper {

	public static ClienteDTO toDTO(Cliente entidad) {
		return new ClienteDTO(entidad.getTipoDocumento(), entidad.getNumeroDocumento(), entidad.getNombres(),
				entidad.getApellidos(), entidad.getIdCuentaU());
	}

	public static Cliente toEntity(ClienteDTO dto) {
		return new Cliente(dto.tipoDocumento(), dto.numeroDocumento(), dto.nombres(), dto.apellidos(),
				dto.idCuentaU());
	}

}
