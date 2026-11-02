package co.edu.unbosque.dto;

import java.time.LocalDate;

public record EdicionDTO(int idEdicion, String codigoImpreso, String nombre, LocalDate fechaLanzamiento, Integer cantidadCartas, String tipoEdicion) {

}
