package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.model.CuentaUsuario;

/**
 * Solo va de entidad a DTO: el DTO no lleva la contraseña, así que no alcanza
 * para reconstruir la cuenta. Las cuentas se crean desde RegistroClienteDTO y
 * RegistroEmpleadoDTO.
 */
public class CuentaUsuarioMapper {

	public static CuentaUsuarioDTO toDTO(CuentaUsuario entidad) {
		return new CuentaUsuarioDTO(entidad.getIdCuentaU(), entidad.getNombreUsuario(), entidad.getEstado(),
				entidad.getFechaUAcceso(), entidad.getCodigoRol());
	}

}
