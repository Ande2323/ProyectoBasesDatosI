package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.model.Ejemplar;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class EjemplarDAOImpl implements EjemplarDAO {

	private static final String COLUMNAS = "id_ejemplar, codigo_etiqueta, ubicacion, estado, precio_sugerido, fecha_ingreso, "
			+ "observaciones_danos, impresion_carta_id_carta, impresion_numero_impresion, condicion_codigo_condicion, "
			+ "salida_consecutivo_salida";

	private static final String INSERTAR = "INSERT INTO ejemplar (codigo_etiqueta, ubicacion, estado, precio_sugerido, "
			+ "fecha_ingreso, observaciones_danos, impresion_carta_id_carta, impresion_numero_impresion, "
			+ "condicion_codigo_condicion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE ejemplar SET ubicacion = ?, condicion_codigo_condicion = ?, "
			+ "observaciones_danos = ? WHERE id_ejemplar = ?";
	private static final String DAR_DE_BAJA = "UPDATE ejemplar SET estado = 'BAJA', "
			+ "observaciones_danos = COALESCE(?, observaciones_danos) WHERE id_ejemplar = ? AND estado = 'DISPONIBLE'";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM ejemplar";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM ejemplar WHERE id_ejemplar = ?";
	private static final String BUSCAR_ETIQUETA = "SELECT " + COLUMNAS + " FROM ejemplar WHERE codigo_etiqueta = ?";

	@Override
	public boolean crearEjemplar(Ejemplar e) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, e.getCodigoEtiqueta());
			JdbcUtil.asignarTexto(ps, 2, e.getUbicacion());
			ps.setString(3, e.getEstado());
			JdbcUtil.asignarDecimal(ps, 4, e.getPrecioSugerido());
			JdbcUtil.asignarFecha(ps, 5, e.getFechaIngreso());
			JdbcUtil.asignarTexto(ps, 6, e.getObservacionesDanos());
			ps.setInt(7, e.getIdCarta());
			ps.setInt(8, e.getNumeroImpresion());
			ps.setString(9, e.getCodigoCondicion());
			ps.executeUpdate();
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				llaves.next();
				e.setIdEjemplar(llaves.getInt(1));
			}
			return true;
		} catch (SQLIntegrityConstraintViolationException ex) {
			return false; // etiqueta repetida, o la impresión o la condición no existen
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean actualizarEjemplar(Ejemplar e, int idEjemplar) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			JdbcUtil.asignarTexto(ps, 1, e.getUbicacion());
			ps.setString(2, e.getCodigoCondicion());
			JdbcUtil.asignarTexto(ps, 3, e.getObservacionesDanos());
			ps.setInt(4, idEjemplar);
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException ex) {
			return false; // la condición no existe
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean darDeBajaEjemplar(int idEjemplar, String observacionesDanos) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(DAR_DE_BAJA)) {
			JdbcUtil.asignarTexto(ps, 1, observacionesDanos);
			ps.setInt(2, idEjemplar);
			return ps.executeUpdate() == 1;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Ejemplar> mostrarEjemplares(String estado, Integer idCarta, Integer numeroImpresion) {
		StringBuilder sql = new StringBuilder(MOSTRAR);
		List<Object> parametros = new ArrayList<>();
		agregarFiltro(sql, parametros, "estado", estado);
		agregarFiltro(sql, parametros, "impresion_carta_id_carta", idCarta);
		agregarFiltro(sql, parametros, "impresion_numero_impresion", numeroImpresion);
		sql.append(" ORDER BY id_ejemplar");

		List<Ejemplar> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearFila(rs));
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return lista;
	}

	@Override
	public Ejemplar buscarEjemplar(int idEjemplar) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idEjemplar);
			return leerUno(ps);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Ejemplar buscarEjemplarPorEtiqueta(String codigoEtiqueta) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR_ETIQUETA)) {
			ps.setString(1, codigoEtiqueta);
			return leerUno(ps);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void agregarFiltro(StringBuilder sql, List<Object> parametros, String columna, Object valor) {
		if (valor != null) {
			sql.append(parametros.isEmpty() ? " WHERE " : " AND ").append(columna).append(" = ?");
			parametros.add(valor);
		}
	}

	private Ejemplar leerUno(PreparedStatement ps) throws SQLException {
		try (ResultSet rs = ps.executeQuery()) {
			return rs.next() ? mapearFila(rs) : null;
		}
	}

	private Ejemplar mapearFila(ResultSet rs) throws SQLException {
		return new Ejemplar(rs.getInt("id_ejemplar"), rs.getString("codigo_etiqueta"), rs.getString("ubicacion"),
				rs.getString("estado"), rs.getBigDecimal("precio_sugerido"), JdbcUtil.leerFecha(rs, "fecha_ingreso"),
				rs.getString("observaciones_danos"), rs.getInt("impresion_carta_id_carta"),
				rs.getInt("impresion_numero_impresion"), rs.getString("condicion_codigo_condicion"),
				JdbcUtil.leerEntero(rs, "salida_consecutivo_salida"));
	}

}
