package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.EmpleadoDAO;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.model.Empleado;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class EmpleadoDAOImpl implements EmpleadoDAO {

	private static final String COLUMNAS = "tipo_documento, numero_documento, nombres, apellidos, cargo, estado, "
			+ "fecha_vinculacion, cuenta_usuario_id_cuenta_u";

	private static final String INSERTAR = "INSERT INTO empleado (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE empleado SET nombres = ?, apellidos = ?, cargo = ?, fecha_vinculacion = ? "
			+ "WHERE tipo_documento = ? AND numero_documento = ?";
	private static final String DAR_DE_BAJA = "UPDATE empleado SET estado = 'RETIRADO' WHERE tipo_documento = ? AND numero_documento = ?";
	private static final String DAR_DE_BAJA_CUENTA = "UPDATE cuenta_usuario SET estado = 'INACTIVA' WHERE id_cuenta_u = "
			+ "(SELECT cuenta_usuario_id_cuenta_u FROM empleado WHERE tipo_documento = ? AND numero_documento = ?)";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM empleado ORDER BY estado, apellidos, nombres";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM empleado WHERE tipo_documento = ? AND numero_documento = ?";
	private static final String BUSCAR_CUENTA = "SELECT " + COLUMNAS + " FROM empleado WHERE cuenta_usuario_id_cuenta_u = ?";

	@Override
	public boolean registrarEmpleado(Empleado empleado, CuentaUsuario cuenta) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				CuentaUsuarioDAOImpl.insertarCuenta(conexion, cuenta);
				empleado.setIdCuentaU(cuenta.getIdCuentaU());
				try (PreparedStatement ps = conexion.prepareStatement(INSERTAR)) {
					ps.setString(1, empleado.getTipoDocumento());
					ps.setString(2, empleado.getNumeroDocumento());
					ps.setString(3, empleado.getNombres());
					ps.setString(4, empleado.getApellidos());
					JdbcUtil.asignarTexto(ps, 5, empleado.getCargo());
					ps.setString(6, empleado.getEstado());
					JdbcUtil.asignarFecha(ps, 7, empleado.getFechaVinculacion());
					ps.setInt(8, empleado.getIdCuentaU());
					ps.executeUpdate();
				}
				conexion.commit();
				return true;
			} catch (SQLException e) {
				conexion.rollback();
				if (!(e instanceof SQLIntegrityConstraintViolationException)) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean actualizarEmpleado(Empleado empleado, String tipoDocumento, String numeroDocumento) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, empleado.getNombres());
			ps.setString(2, empleado.getApellidos());
			JdbcUtil.asignarTexto(ps, 3, empleado.getCargo());
			JdbcUtil.asignarFecha(ps, 4, empleado.getFechaVinculacion());
			ps.setString(5, tipoDocumento);
			ps.setString(6, numeroDocumento);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean darDeBajaEmpleado(String tipoDocumento, String numeroDocumento) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				for (String sql : new String[] { DAR_DE_BAJA, DAR_DE_BAJA_CUENTA }) {
					try (PreparedStatement ps = conexion.prepareStatement(sql)) {
						ps.setString(1, tipoDocumento);
						ps.setString(2, numeroDocumento);
						if (ps.executeUpdate() != 1) {
							conexion.rollback();
							return false;
						}
					}
				}
				conexion.commit();
				return true;
			} catch (SQLException e) {
				conexion.rollback();
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Empleado> mostrarEmpleados() {
		return consultar(MOSTRAR);
	}

	@Override
	public Empleado buscarEmpleado(String tipoDocumento, String numeroDocumento) {
		List<Empleado> lista = consultar(BUSCAR, tipoDocumento, numeroDocumento);
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public Empleado buscarEmpleadoPorCuenta(int idCuentaU) {
		List<Empleado> lista = consultar(BUSCAR_CUENTA, String.valueOf(idCuentaU));
		return lista.isEmpty() ? null : lista.get(0);
	}

	private List<Empleado> consultar(String sql, String... parametros) {
		List<Empleado> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int i = 0; i < parametros.length; i++) {
				ps.setString(i + 1, parametros[i]);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Empleado(rs.getString("tipo_documento"), rs.getString("numero_documento"),
							rs.getString("nombres"), rs.getString("apellidos"), rs.getString("cargo"),
							rs.getString("estado"), JdbcUtil.leerFecha(rs, "fecha_vinculacion"),
							rs.getInt("cuenta_usuario_id_cuenta_u")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
