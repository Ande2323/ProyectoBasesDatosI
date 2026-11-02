package co.edu.unbosque.dto;

/**
 * Lo que llena un cliente al registrarse en la página: sus datos y los de su
 * cuenta. La contraseña llega en claro solo aquí y se cifra antes de guardarla.
 */
public record RegistroClienteDTO(String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
		String nombreUsuario, String contrasena) {

}
