package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import co.edu.unbosque.dao.ImagenEjemplarDAO;
import co.edu.unbosque.util.ConexionBD;

public class ImagenEjemplarDAOImpl implements ImagenEjemplarDAO {

	private static final String GUARDAR = "INSERT INTO imagen_ejemplar (id_ejemplar, contenido) VALUES (?, ?) "
			+ "ON DUPLICATE KEY UPDATE contenido = VALUES(contenido)";
	private static final String LEER = "SELECT contenido FROM imagen_ejemplar WHERE id_ejemplar = ?";
	private static final String EXISTE = "SELECT 1 FROM imagen_ejemplar WHERE id_ejemplar = ?";
	private static final String MOSTRAR_IDS = "SELECT id_ejemplar FROM imagen_ejemplar";

	@Override
	public boolean guardarImagen(int idEjemplar, byte[] contenido) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(GUARDAR)) {
			ps.setInt(1, idEjemplar);
			ps.setBytes(2, contenido);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] leerImagen(int idEjemplar) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(LEER)) {
			ps.setInt(1, idEjemplar);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getBytes("contenido");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean existeImagen(int idEjemplar) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(EXISTE)) {
			ps.setInt(1, idEjemplar);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** No trae los archivos: las listas solo necesitan saber quién tiene imagen. */
	@Override
	public Set<Integer> mostrarIdsConImagen() {
		Set<Integer> ids = new HashSet<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(MOSTRAR_IDS);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ids.add(rs.getInt("id_ejemplar"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ids;
	}

}
