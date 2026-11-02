package co.edu.unbosque.dto;

import java.time.LocalDateTime;

/** Sin la contraseña: el hash nunca sale del backend. */
public record CuentaUsuarioDTO(int idCuentaU, String nombreUsuario, String estado, LocalDateTime fechaUAcceso,
		String codigoRol) {

}
