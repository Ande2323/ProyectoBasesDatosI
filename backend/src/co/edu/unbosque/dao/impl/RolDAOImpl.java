package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.RolDAO;
import co.edu.unbosque.model.Rol;
import co.edu.unbosque.util.ConexionBD;

public class RolDAOImpl implements RolDAO {

	private static final String MOSTRAR = "SELECT codigo_rol, nombre FROM rol ORDER BY codigo_rol";
	private static final String BUSCAR = "SELECT codigo_rol, nombre FROM rol WHERE codigo_rol = ?";

	@Override
	public List<Rol> mostrarRoles() {
		List<Rol> lista = new ArrayList<>();
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
	public Rol buscarRol(String codigoRol) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setString(1, codigoRol);
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

	private Rol mapearFila(ResultSet rs) throws SQLException {
		return new Rol(rs.getString("codigo_rol"), rs.getString("nombre"));
	}

}
