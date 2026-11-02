package co.edu.unbosque.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * La fecha de "hoy" siempre según la tienda en Bogotá, aunque el equipo donde
 * corre el backend tenga otra zona horaria.
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
