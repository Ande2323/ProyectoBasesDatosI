package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.RarezaDAO;
import co.edu.unbosque.model.Rareza;
import co.edu.unbosque.util.ConexionBD;

public class RarezaDAOImpl implements RarezaDAO {

	private static final String INSERTAR = "INSERT INTO rareza (codigo, nombre) VALUES (?, ?)";
	private static final String ACTUALIZAR = "UPDATE rareza SET codigo = ?, nombre = ? WHERE id_rareza = ?";
	private static final String MOSTRAR = "SELECT id_rareza, codigo, nombre FROM rareza ORDER BY id_rareza";
	private static final String BUSCAR = "SELECT id_rareza, codigo, nombre FROM rareza WHERE id_rareza = ?";

	/** Inserta y deja en rareza el idRareza que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearRareza(Rareza rareza) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_rareza" })) {
			ps.setString(1, rareza.getCodigo());
			ps.setString(2, rareza.getNombre());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					rareza.setIdRareza(llaves.getInt(1));
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
	public boolean actualizarRareza(Rareza rareza, int idRareza) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, rareza.getCodigo());
			ps.setString(2, rareza.getNombre());
			ps.setInt(3, idRareza);
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
	public List<Rareza> mostrarRarezas() {
		List<Rareza> lista = new ArrayList<>();
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
	public Rareza buscarRareza(int idRareza) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idRareza);
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

	private Rareza mapearFila(ResultSet rs) throws SQLException {
		return new Rareza(rs.getInt("id_rareza"), rs.getString("codigo"), rs.getString("nombre"));
	}

}
