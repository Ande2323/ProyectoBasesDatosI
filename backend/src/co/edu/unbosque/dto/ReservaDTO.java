package co.edu.unbosque.dto;

import java.time.LocalDate;
import java.util.List;

public record ReservaDTO(int consecutivo, LocalDate fechaCreacion, String estado, LocalDate fechaVencimiento,
		String clienteTipoDocumento, String clienteNumeroDocumento, Integer consecutivoSalida,
		List<Integer> ejemplares) {

}
