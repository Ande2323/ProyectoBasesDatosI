package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.FechaUtil;

public class CuentaUsuarioDAOImpl implements CuentaUsuarioDAO {

	private static final String COLUMNAS = "id_cuenta_u, nombre_usuario, contrasena, estado, fecha_u_acceso, rol_codigo_rol";

	private static final String INSERTAR = "INSERT INTO cuenta_usuario (nombre_usuario, contrasena, estado, rol_codigo_rol) VALUES (?, ?, ?, ?)";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM cuenta_usuario WHERE id_cuenta_u = ?";
	private static final String BUSCAR_USUARIO = "SELECT " + COLUMNAS + " FROM cuenta_usuario WHERE nombre_usuario = ?";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM cuenta_usuario ORDER BY id_cuenta_u";
	private static final String CAMBIAR_ESTADO = "UPDATE cuenta_usuario SET estado = ? WHERE id_cuenta_u = ?";
	private static final String CAMBIAR_ROL = "UPDATE cuenta_usuario SET rol_codigo_rol = ? WHERE id_cuenta_u = ?";
	private static final String CAMBIAR_CONTRASENA = "UPDATE cuenta_usuario SET contrasena = ? WHERE id_cuenta_u = ?";
	private static final String REGISTRAR_ACCESO = "UPDATE cuenta_usuario SET fecha_u_acceso = ? WHERE id_cuenta_u = ?";
	private static final String CONTAR_ADMINS = "SELECT COUNT(*) FROM cuenta_usuario WHERE rol_codigo_rol = 'ADM' AND estado = 'ACTIVA'";

	/**
	 * Inserta la cuenta dentro de una transacción que abrió otro DAO (el del
	 * cliente o el del empleado). El id que genera MySQL queda en la cuenta.
	 */
	static void insertarCuenta(Connection conexion, CuentaUsuario cuenta) throws SQLException {
		try (PreparedStatement ps = conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, cuenta.getNombreUsuario());
			ps.setString(2, cuenta.getContrasena());
			ps.setString(3, cuenta.getEstado());
			ps.setString(4, cuenta.getCodigoRol());
			ps.executeUpdate();
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				llaves.next();
				cuenta.setIdCuentaU(llaves.getInt(1));
			}
		}
	}

	@Override
	public CuentaUsuario buscarCuenta(int idCuentaU) {
		List<CuentaUsuario> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idCuentaU);
			leerFilas(ps, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public CuentaUsuario buscarCuentaPorUsuario(String nombreUsuario) {
		List<CuentaUsuario> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR_USUARIO)) {
			ps.setString(1, nombreUsuario);
			leerFilas(ps, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public List<CuentaUsuario> mostrarCuentas() {
		List<CuentaUsuario> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(MOSTRAR)) {
			leerFilas(ps, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean cambiarEstadoCuenta(int idCuentaU, String estado) {
		return actualizar(CAMBIAR_ESTADO, estado, idCuentaU);
	}

	@Override
	public boolean cambiarRolCuenta(int idCuentaU, String codigoRol) {
		return actualizar(CAMBIAR_ROL, codigoRol, idCuentaU);
	}

	@Override
	public boolean cambiarContrasena(int idCuentaU, String contrasenaCifrada) {
		return actualizar(CAMBIAR_CONTRASENA, contrasenaCifrada, idCuentaU);
	}

	@Override
	public void registrarAcceso(int idCuentaU) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(REGISTRAR_ACCESO)) {
			ps.setTimestamp(1, Timestamp.valueOf(FechaUtil.ahora()));
			ps.setInt(2, idCuentaU);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int contarAdministradoresActivos() {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(CONTAR_ADMINS);
				ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ----------------------------------------------------------------- apoyo

	private boolean actualizar(String sql, String valor, int idCuentaU) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setString(1, valor);
			ps.setInt(2, idCuentaU);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void leerFilas(PreparedStatement ps, List<CuentaUsuario> lista) throws SQLException {
		try (ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(mapearFila(rs));
			}
		}
	}

	private CuentaUsuario mapearFila(ResultSet rs) throws SQLException {
		Timestamp acceso = rs.getTimestamp("fecha_u_acceso");
		return new CuentaUsuario(rs.getInt("id_cuenta_u"), rs.getString("nombre_usuario"), rs.getString("contrasena"),
				rs.getString("estado"), acceso == null ? null : acceso.toLocalDateTime(), rs.getString("rol_codigo_rol"));
	}

}
