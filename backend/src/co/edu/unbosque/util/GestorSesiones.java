package co.edu.unbosque.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sesiones activas en memoria. El documento de supuestos excluye las sesiones
 * del modelo (sección G), así que no se guardan en la base: si el servidor se
 * reinicia, todos vuelven a iniciar sesión.
 *
 * Cada sesión se identifica con un token aleatorio de 256 bits que Angular
 * envía en la cabecera "Authorization: Bearer <token>".
 */
public class GestorSesiones {

	/** Lo que el backend sabe de quien está conectado. */
	public record SesionActiva(String token, int idCuentaU, String nombreUsuario, String codigoRol,
			String tipoDocumento, String numeroDocumento, LocalDateTime expira) {

		public boolean esCliente() {
			return "CLI".equals(codigoRol);
		}

		public boolean esAdmin() {
			return "ADM".equals(codigoRol);
		}
	}

	private static final Map<String, SesionActiva> SESIONES = new ConcurrentHashMap<>();
	private static final SecureRandom ALEATORIO = new SecureRandom();

	public static SesionActiva abrirSesion(int idCuentaU, String nombreUsuario, String codigoRol,
			String tipoDocumento, String numeroDocumento) {
		byte[] bytes = new byte[32];
		ALEATORIO.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		int horas = Integer.parseInt(Configuracion.obtener("sesion.horas", "8"));
		SesionActiva sesion = new SesionActiva(token, idCuentaU, nombreUsuario, codigoRol, tipoDocumento,
				numeroDocumento, FechaUtil.ahora().plusHours(horas));
		SESIONES.put(token, sesion);
		return sesion;
	}

	/** Retorna la sesión del token, o null si no existe o ya venció. */
	public static SesionActiva buscarSesion(String token) {
		if (token == null) {
			return null;
		}
		SesionActiva sesion = SESIONES.get(token);
		if (sesion == null) {
			return null;
		}
		if (sesion.expira().isBefore(FechaUtil.ahora())) {
			SESIONES.remove(token);
			return null;
		}
		return sesion;
	}

	public static void cerrarSesion(String token) {
		if (token != null) {
			SESIONES.remove(token);
		}
	}

	/** Al dar de baja una cuenta o cambiarle el rol, sus sesiones dejan de servir. */
	public static void cerrarSesionesDe(int idCuentaU) {
		SESIONES.values().removeIf(s -> s.idCuentaU() == idCuentaU);
	}
}
