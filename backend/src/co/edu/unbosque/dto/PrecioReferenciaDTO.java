package co.edu.unbosque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrecioReferenciaDTO(int idCarta, int numeroImpresion, LocalDate fecha, BigDecimal valor,
		String fuenteCotizacion) {

}
