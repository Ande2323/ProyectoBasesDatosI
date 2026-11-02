package co.edu.unbosque.dto;

import java.math.BigDecimal;

/** Un ejemplar dentro de una reserva, con lo que el cliente necesita para reconocerlo. */
public record ApartadoDTO(int idEjemplar, String codigoEtiqueta, int idCarta, String nombreCarta,
		String codigoCondicion, BigDecimal precioSugerido) {

}
