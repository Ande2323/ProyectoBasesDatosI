package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.TipoDAO;
import co.edu.unbosque.model.Tipo;
import co.edu.unbosque.util.ConexionBD;

public class TipoDAOImpl implements TipoDAO {

	private static final String INSERTAR = "INSERT INTO tipo (nivel, codigo, nombre) VALUES (?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE tipo SET nivel = ?, codigo = ?, nombre = ? WHERE id_tipo = ?";
	private static final String ELIMINAR = "DELETE FROM tipo WHERE id_tipo = ? AND NOT EXISTS "
			+ "(SELECT 1 FROM carta_tipo WHERE tipo_id_tipo = ?)";
	private static final String MOSTRAR = "SELECT id_tipo, nivel, codigo, nombre FROM tipo ORDER BY nivel, nombre";
	private static final String BUSCAR = "SELECT id_tipo, nivel, codigo, nombre FROM tipo WHERE id_tipo = ?";

	/** Inserta y deja en tipo el idTipo que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearTipo(Tipo tipo) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_tipo" })) {
			ps.setInt(1, tipo.getNivel());
			ps.setString(2, tipo.getCodigo());
			ps.setString(3, tipo.getNombre());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					tipo.setIdTipo(llaves.getInt(1));
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
	public boolean actualizarTipo(Tipo tipo, int idTipo) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setInt(1, tipo.getNivel());
			ps.setString(2, tipo.getCodigo());
			ps.setString(3, tipo.getNombre());
			ps.setInt(4, idTipo);
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
	public boolean eliminarTipo(int idTipo) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ELIMINAR)) {
			ps.setInt(1, idTipo);
			ps.setInt(2, idTipo);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // está en uso
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Tipo> mostrarTipos() {
		List<Tipo> lista = new ArrayList<>();
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
	public Tipo buscarTipo(int idTipo) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idTipo);
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

	private Tipo mapearFila(ResultSet rs) throws SQLException {
		return new Tipo(rs.getInt("id_tipo"), rs.getInt("nivel"), rs.getString("codigo"), rs.getString("nombre"));
	}

}
