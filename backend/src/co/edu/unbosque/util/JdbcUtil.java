package co.edu.unbosque.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

/**
 * Ayudas para columnas que admiten NULL. JDBC devuelve 0 en vez de null para
 * los números vacíos, así que aquí se maneja ese caso en un solo lugar.
 */
public class JdbcUtil {

	public static void asignarEntero(PreparedStatement ps, int indice, Integer valor) throws SQLException {
		if (valor == null) {
			ps.setNull(indice, Types.INTEGER);
		} else {
			ps.setInt(indice, valor);
		}
	}

	public static Integer leerEntero(ResultSet rs, String columna) throws SQLException {
		int valor = rs.getInt(columna);
		return rs.wasNull() ? null : valor;
	}

	public static void asignarFecha(PreparedStatement ps, int indice, LocalDate valor) throws SQLException {
		if (valor == null) {
			ps.setNull(indice, Types.DATE);
		} else {
			ps.setDate(indice, Date.valueOf(valor));
		}
	}

	public static LocalDate leerFecha(ResultSet rs, String columna) throws SQLException {
		Date valor = rs.getDate(columna);
		return valor == null ? null : valor.toLocalDate();
	}

	public static void asignarDecimal(PreparedStatement ps, int indice, BigDecimal valor) throws SQLException {
		if (valor == null) {
			ps.setNull(indice, Types.DECIMAL);
		} else {
			ps.setBigDecimal(indice, valor);
		}
	}

	public static void asignarTexto(PreparedStatement ps, int indice, String valor) throws SQLException {
		if (valor == null || valor.isBlank()) {
			ps.setNull(indice, Types.VARCHAR);
		} else {
			ps.setString(indice, valor);
		}
	}
}
