package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.CondicionDAO;
import co.edu.unbosque.model.Condicion;
import co.edu.unbosque.util.ConexionBD;

public class CondicionDAOImpl implements CondicionDAO {

	private static final String INSERTAR = "INSERT INTO condicion (codigo_condicion, nombre, factor_ajuste) VALUES (?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE condicion SET nombre = ?, factor_ajuste = ? WHERE codigo_condicion = ?";
	private static final String MOSTRAR = "SELECT codigo_condicion, nombre, factor_ajuste FROM condicion ORDER BY factor_ajuste DESC";
	private static final String BUSCAR = "SELECT codigo_condicion, nombre, factor_ajuste FROM condicion WHERE codigo_condicion = ?";

	@Override
	public boolean crearCondicion(Condicion c) {
		if (buscarCondicion(c.getCodigoCondicion()) != null) {
			return false;
		}
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR)) {
			ps.setString(1, c.getCodigoCondicion());
			ps.setString(2, c.getNombre());
			ps.setDouble(3, c.getFactorAjuste());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean actualizarCondicion(Condicion c, String codigoCondicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, c.getNombre());
			ps.setDouble(2, c.getFactorAjuste());
			ps.setString(3, codigoCondicion);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Solo borra si ningún ejemplar la usa: si la usan, MySQL lo impide. */
	@Override
	public List<Condicion> mostrarCondiciones() {
		List<Condicion> lista = new ArrayList<>();
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
	public Condicion buscarCondicion(String codigoCondicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setString(1, codigoCondicion);
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

	private Condicion mapearFila(ResultSet rs) throws SQLException {
		return new Condicion(rs.getString("codigo_condicion"), rs.getString("nombre"), rs.getDouble("factor_ajuste"));
	}

}
