package co.edu.unbosque.dto;

import java.time.LocalDate;

/** Lo que llena el administrador al registrar un empleado. codigoRol: EMP o ADM. */
public record RegistroEmpleadoDTO(String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
		String cargo, LocalDate fechaVinculacion, String nombreUsuario, String contrasena, String codigoRol) {

}
