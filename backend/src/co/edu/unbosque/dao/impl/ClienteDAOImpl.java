package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.ClienteDAO;
import co.edu.unbosque.model.Cliente;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.util.ConexionBD;

public class ClienteDAOImpl implements ClienteDAO {

	private static final String COLUMNAS = "tipo_documento, numero_documento, nombres, apellidos, cuenta_usuario_id_cuenta_u";

	private static final String INSERTAR = "INSERT INTO cliente (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE cliente SET nombres = ?, apellidos = ? WHERE tipo_documento = ? AND numero_documento = ?";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM cliente ORDER BY apellidos, nombres";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM cliente WHERE tipo_documento = ? AND numero_documento = ?";
	private static final String BUSCAR_CUENTA = "SELECT " + COLUMNAS + " FROM cliente WHERE cuenta_usuario_id_cuenta_u = ?";

	@Override
	public boolean registrarCliente(Cliente cliente, CuentaUsuario cuenta) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				CuentaUsuarioDAOImpl.insertarCuenta(conexion, cuenta);
				cliente.setIdCuentaU(cuenta.getIdCuentaU());
				try (PreparedStatement ps = conexion.prepareStatement(INSERTAR)) {
					ps.setString(1, cliente.getTipoDocumento());
					ps.setString(2, cliente.getNumeroDocumento());
					ps.setString(3, cliente.getNombres());
					ps.setString(4, cliente.getApellidos());
					ps.setInt(5, cliente.getIdCuentaU());
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
	public boolean actualizarCliente(Cliente cliente, String tipoDocumento, String numeroDocumento) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			ps.setString(1, cliente.getNombres());
			ps.setString(2, cliente.getApellidos());
			ps.setString(3, tipoDocumento);
			ps.setString(4, numeroDocumento);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Cliente> mostrarClientes() {
		return consultar(MOSTRAR);
	}

	@Override
	public Cliente buscarCliente(String tipoDocumento, String numeroDocumento) {
		List<Cliente> lista = consultar(BUSCAR, tipoDocumento, numeroDocumento);
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public Cliente buscarClientePorCuenta(int idCuentaU) {
		List<Cliente> lista = consultar(BUSCAR_CUENTA, String.valueOf(idCuentaU));
		return lista.isEmpty() ? null : lista.get(0);
	}

	private List<Cliente> consultar(String sql, String... parametros) {
		List<Cliente> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int i = 0; i < parametros.length; i++) {
				ps.setString(i + 1, parametros[i]);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Cliente(rs.getString("tipo_documento"), rs.getString("numero_documento"),
							rs.getString("nombres"), rs.getString("apellidos"), rs.getInt("cuenta_usuario_id_cuenta_u")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
