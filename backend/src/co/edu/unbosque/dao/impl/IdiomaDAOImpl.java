package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.IdiomaDAO;
import co.edu.unbosque.model.Idioma;
import co.edu.unbosque.util.ConexionBD;

public class IdiomaDAOImpl implements IdiomaDAO {

	private static final String INSERTAR = "INSERT INTO idioma (codigo, nombre) VALUES (?, ?)";
	private static final String ACTUALIZAR = "UPDATE idioma SET codigo = ?, nombre = ? WHERE id_idioma = ?";
	private static final String MOSTRAR = "SELECT id_idioma, codigo, nombre FROM idioma ORDER BY nombre";
	private static final String BUSCAR = "SELECT id_idioma, codigo, nombre FROM idioma WHERE id_idioma = ?";

	/** Inserta y deja en idioma el idIdioma que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearIdioma(Idioma idioma) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_idioma" })) {
			ps.setString(1, idioma.getCodigo());
			ps.setString(2, idioma.getNombre());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					idioma.setIdIdioma(llaves.getInt(1));
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
	public boolean actualizarIdioma(Idioma idioma, int idIdioma) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, idioma.getCodigo());
			ps.setString(2, idioma.getNombre());
			ps.setInt(3, idIdioma);
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
	public List<Idioma> mostrarIdiomas() {
		List<Idioma> lista = new ArrayList<>();
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
	public Idioma buscarIdioma(int idIdioma) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idIdioma);
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

	private Idioma mapearFila(ResultSet rs) throws SQLException {
		return new Idioma(rs.getInt("id_idioma"), rs.getString("codigo"), rs.getString("nombre"));
	}

}
