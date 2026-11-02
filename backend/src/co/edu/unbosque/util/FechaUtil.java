package co.edu.unbosque.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * La fecha de "hoy" siempre según la tienda en Bogotá. Una máquina de AWS suele
 * estar en UTC: sin esto, desde las 7 p.m. el sistema creería que ya es mañana.
 */
public class FechaUtil {

	private static ZoneId zona() {
		return ZoneId.of(Configuracion.obtener("zona.horaria", "America/Bogota"));
	}

	public static LocalDate hoy() {
		return LocalDate.now(zona());
	}

	public static LocalDateTime ahora() {
		return LocalDateTime.now(zona());
	}
}
