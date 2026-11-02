package co.edu.unbosque.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropiedadesUtil {

	private final Properties propiedades;

	public PropiedadesUtil(String nombreArchivo) {
		propiedades = new Properties();
		try (FileInputStream fis = new FileInputStream(nombreArchivo)) {
			propiedades.load(fis);
		} catch (IOException e) {
			System.err.println("Error al cargar el archivo de propiedades: " + nombreArchivo);
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene el valor de una clave del archivo de propiedades.
	 *
	 * @param clave La clave a buscar (ej: "admin.password").
	 * @return El valor asociado a la clave, o null si no se encuentra.
	 */
	public String obtenerPropiedad(String clave) {
		return propiedades.getProperty(clave);
	}
}