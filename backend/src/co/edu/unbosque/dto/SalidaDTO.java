package co.edu.unbosque.dto;

import java.time.LocalDate;
import java.util.List;

public record SalidaDTO(int consecutivoSalida, String motivo, String observaciones, LocalDate fecha,
		String clienteTipoDocumento, String clienteNumeroDocumento, String empleadoTipoDocumento,
		String empleadoNumeroDocumento, List<Integer> ejemplares, Integer consecutivoReserva) {

}
