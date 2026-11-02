package co.edu.unbosque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EjemplarDTO(int idEjemplar, String codigoEtiqueta, String ubicacion, String estado,
		BigDecimal precioSugerido, LocalDate fechaIngreso, String observacionesDanos, int idCarta, int numeroImpresion,
		String codigoCondicion, Integer consecutivoSalida) {

}
