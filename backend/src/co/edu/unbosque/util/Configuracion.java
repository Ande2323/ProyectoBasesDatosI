package co.edu.unbosque.util;

/**
 * Punto único de acceso a config/configuracion.properties (credenciales de la
 * base de datos, puerto del servidor y origen permitido para Angular).
 */
public class Configuracion {

	private static final String RUTA_ARCHIVO = "config/configuracion.properties";
	private static PropiedadesUtil propiedades;

	public static String obtener(String clave, String valorPorDefecto) {
		if (propiedades == null) {
			propiedades = new PropiedadesUtil(RUTA_ARCHIVO);
		}
		String valor = propiedades.obtenerPropiedad(clave);
		if (valor == null || valor.isBlank()) {
			return valorPorDefecto;
		}
		return valor.trim();
	}

	public static String obtener(String clave) {
		return obtener(clave, null);
	}
}
