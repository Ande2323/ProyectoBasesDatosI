package co.edu.unbosque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexiones JDBC contra MySQL. Cada operación del DAO pide su propia
 * conexión y la cierra con try-with-resources. Requiere el driver
 * mysql-connector-j en lib/.
 */
public class ConexionBD {

	public static Connection obtenerConexion() throws SQLException {
		String url = Configuracion.obtener("bd.url");
		String usuario = Configuracion.obtener("bd.usuario");
		String contrasena = Configuracion.obtener("bd.contrasena");
		if (url == null) {
			throw new SQLException("Falta bd.url en config/configuracion.properties");
		}
		return DriverManager.getConnection(url, usuario, contrasena);
	}

	public static boolean probarConexion() {
		try (Connection conexion = obtenerConexion()) {
			return conexion.isValid(3);
		} catch (SQLException e) {
			System.err.println("No fue posible conectar con la base de datos: " + e.getMessage());
			return false;
		}
	}
}
