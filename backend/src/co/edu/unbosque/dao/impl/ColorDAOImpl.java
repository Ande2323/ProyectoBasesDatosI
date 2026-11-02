package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.ColorDAO;
import co.edu.unbosque.model.Color;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class ColorDAOImpl implements ColorDAO {

	private static final String INSERTAR = "INSERT INTO color (nombre_color, orden_canonico) VALUES (?, ?)";
	private static final String ACTUALIZAR = "UPDATE color SET nombre_color = ?, orden_canonico = ? WHERE id_color = ?";
	private static final String MOSTRAR = "SELECT id_color, nombre_color, orden_canonico FROM color ORDER BY orden_canonico";
	private static final String BUSCAR = "SELECT id_color, nombre_color, orden_canonico FROM color WHERE id_color = ?";

	/** Inserta y deja en color el idColor que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearColor(Color color) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_color" })) {
			ps.setString(1, color.getNombreColor());
			JdbcUtil.asignarEntero(ps, 2, color.getOrdenCanonico());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					color.setIdColor(llaves.getInt(1));
				}
			}
			return true;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // código repetido
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean actualizarColor(Color color, int idColor) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, color.getNombreColor());
			JdbcUtil.asignarEntero(ps, 2, color.getOrdenCanonico());
			ps.setInt(3, idColor);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // el nuevo valor choca con otro registro
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Solo borra si ninguna carta lo usa. El ON DELETE CASCADE del DDL lo quitaría de las cartas sin avisar. */
	@Override
	public List<Color> mostrarColores() {
		List<Color> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(MOSTRAR);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(mapearFila(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public Color buscarColor(int idColor) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idColor);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapearFila(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Color mapearFila(ResultSet rs) throws SQLException {
		return new Color(rs.getInt("id_color"), rs.getString("nombre_color"), JdbcUtil.leerEntero(rs, "orden_canonico"));
	}

}
