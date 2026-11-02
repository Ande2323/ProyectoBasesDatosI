package co.edu.unbosque.dto;

import java.time.LocalDate;

public record EmpleadoDTO(String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
		String cargo, String estado, LocalDate fechaVinculacion, int idCuentaU) {

}
