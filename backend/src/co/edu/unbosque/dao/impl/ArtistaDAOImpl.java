package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.ArtistaDAO;
import co.edu.unbosque.model.Artista;
import co.edu.unbosque.util.ConexionBD;

public class ArtistaDAOImpl implements ArtistaDAO {

	private static final String INSERTAR = "INSERT INTO artista (nombre) VALUES (?)";
	private static final String ACTUALIZAR = "UPDATE artista SET nombre = ? WHERE id_artista = ?";
	private static final String ELIMINAR = "DELETE FROM artista WHERE id_artista = ?";
	private static final String MOSTRAR = "SELECT id_artista, nombre FROM artista ORDER BY nombre";
	private static final String BUSCAR = "SELECT id_artista, nombre FROM artista WHERE id_artista = ?";

	/** Inserta y deja en artista el idArtista que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearArtista(Artista artista) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_artista" })) {
			ps.setString(1, artista.getNombre());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					artista.setIdArtista(llaves.getInt(1));
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
	public boolean actualizarArtista(Artista artista, int idArtista) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, artista.getNombre());
			ps.setInt(2, idArtista);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // el nuevo valor choca con otro registro
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Solo borra si nadie lo usa: si hay registros que lo referencian, MySQL lo impide. */
	@Override
	public boolean eliminarArtista(int idArtista) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ELIMINAR)) {
			ps.setInt(1, idArtista);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // está en uso
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Artista> mostrarArtistas() {
		List<Artista> lista = new ArrayList<>();
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
	public Artista buscarArtista(int idArtista) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idArtista);
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

	private Artista mapearFila(ResultSet rs) throws SQLException {
		return new Artista(rs.getInt("id_artista"), rs.getString("nombre"));
	}

}
