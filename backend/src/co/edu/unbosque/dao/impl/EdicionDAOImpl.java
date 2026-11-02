package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.EdicionDAO;
import co.edu.unbosque.model.Edicion;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class EdicionDAOImpl implements EdicionDAO {

	private static final String INSERTAR = "INSERT INTO edicion (codigo_impreso, nombre, fecha_lanzamiento, cantidad_cartas, tipo_edicion) VALUES (?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE edicion SET codigo_impreso = ?, nombre = ?, fecha_lanzamiento = ?, cantidad_cartas = ?, tipo_edicion = ? WHERE id_edicion = ?";
	private static final String ELIMINAR = "DELETE FROM edicion WHERE id_edicion = ?";
	private static final String MOSTRAR = "SELECT id_edicion, codigo_impreso, nombre, fecha_lanzamiento, cantidad_cartas, tipo_edicion FROM edicion ORDER BY fecha_lanzamiento DESC, nombre";
	private static final String BUSCAR = "SELECT id_edicion, codigo_impreso, nombre, fecha_lanzamiento, cantidad_cartas, tipo_edicion FROM edicion WHERE id_edicion = ?";

	/** Inserta y deja en edicion el idEdicion que generó MySQL (AUTO_INCREMENT). */
	@Override
	public boolean crearEdicion(Edicion edicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_edicion" })) {
			ps.setString(1, edicion.getCodigoImpreso());
			ps.setString(2, edicion.getNombre());
			JdbcUtil.asignarFecha(ps, 3, edicion.getFechaLanzamiento());
			JdbcUtil.asignarEntero(ps, 4, edicion.getCantidadCartas());
			JdbcUtil.asignarTexto(ps, 5, edicion.getTipoEdicion());
			if (ps.executeUpdate() != 1) {
				return false;
			}
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				if (llaves.next()) {
					edicion.setIdEdicion(llaves.getInt(1));
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
	public boolean actualizarEdicion(Edicion edicion, int idEdicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, edicion.getCodigoImpreso());
			ps.setString(2, edicion.getNombre());
			JdbcUtil.asignarFecha(ps, 3, edicion.getFechaLanzamiento());
			JdbcUtil.asignarEntero(ps, 4, edicion.getCantidadCartas());
			JdbcUtil.asignarTexto(ps, 5, edicion.getTipoEdicion());
			ps.setInt(6, idEdicion);
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
	public boolean eliminarEdicion(int idEdicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ELIMINAR)) {
			ps.setInt(1, idEdicion);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // está en uso
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Edicion> mostrarEdiciones() {
		List<Edicion> lista = new ArrayList<>();
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
	public Edicion buscarEdicion(int idEdicion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idEdicion);
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

	private Edicion mapearFila(ResultSet rs) throws SQLException {
		return new Edicion(rs.getInt("id_edicion"), rs.getString("codigo_impreso"), rs.getString("nombre"), JdbcUtil.leerFecha(rs, "fecha_lanzamiento"), JdbcUtil.leerEntero(rs, "cantidad_cartas"), rs.getString("tipo_edicion"));
	}

}
