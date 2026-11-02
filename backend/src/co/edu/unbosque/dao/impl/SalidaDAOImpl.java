package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import co.edu.unbosque.dao.SalidaDAO;
import co.edu.unbosque.model.Salida;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class SalidaDAOImpl implements SalidaDAO {

	private static final String CONSULTA = "SELECT s.consecutivo_salida, s.motivo, s.observaciones, s.fecha, "
			+ "s.cliente_tipo_documento, s.cliente_numero_documento, s.empleado_tipo_documento, "
			+ "s.empleado_numero_documento, r.consecutivo AS consecutivo_reserva FROM salida s "
			+ "LEFT JOIN reserva r ON r.salida_consecutivo_salida = s.consecutivo_salida";

	private static final String INSERTAR = "INSERT INTO salida (motivo, observaciones, fecha, cliente_tipo_documento, "
			+ "cliente_numero_documento, empleado_tipo_documento, empleado_numero_documento) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String BLOQUEAR_EJEMPLAR = "SELECT estado FROM ejemplar WHERE id_ejemplar = ? FOR UPDATE";
	private static final String BLOQUEAR_RESERVA = "SELECT estado, fecha_vencimiento FROM reserva WHERE consecutivo = ? FOR UPDATE";
	private static final String EJEMPLARES_DE_RESERVA = "SELECT id_ejemplar FROM ejemplar_reserva WHERE consecutivo = ?";
	private static final String ENTREGAR = "UPDATE ejemplar SET estado = 'VENDIDO', salida_consecutivo_salida = ? WHERE id_ejemplar = ?";
	private static final String LIBERAR = "UPDATE ejemplar SET estado = 'DISPONIBLE' WHERE id_ejemplar = ? AND estado = 'RESERVADO'";
	private static final String CERRAR_RESERVA = "UPDATE reserva SET estado = 'ENTREGADA', salida_consecutivo_salida = ? WHERE consecutivo = ?";
	private static final String MOSTRAR = CONSULTA + " ORDER BY s.consecutivo_salida DESC";
	private static final String BUSCAR = CONSULTA + " WHERE s.consecutivo_salida = ?";
	private static final String TODOS_LOS_RETIROS = "SELECT salida_consecutivo_salida, id_ejemplar FROM ejemplar "
			+ "WHERE salida_consecutivo_salida IS NOT NULL ORDER BY salida_consecutivo_salida, id_ejemplar";

	@Override
	public boolean registrarEntregaDirecta(Salida s) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				if (!ejemplaresEnEstado(conexion, s.getEjemplares(), "DISPONIBLE")) {
					conexion.rollback();
					return false;
				}
				insertarYEntregar(conexion, s);
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
	public boolean registrarReclamo(Salida s) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				if (!reservaReclamable(conexion, s) || !ejemplaresEnEstado(conexion, s.getEjemplares(), "RESERVADO")) {
					conexion.rollback();
					return false;
				}
				Set<Integer> apartados = ejemplaresDeReserva(conexion, s.getConsecutivoReserva());
				if (!apartados.containsAll(s.getEjemplares())) {
					conexion.rollback();
					return false;
				}
				insertarYEntregar(conexion, s);
				apartados.removeAll(s.getEjemplares());
				for (int idEjemplar : apartados) {
					try (PreparedStatement ps = conexion.prepareStatement(LIBERAR)) {
						ps.setInt(1, idEjemplar);
						ps.executeUpdate();
					}
				}
				try (PreparedStatement ps = conexion.prepareStatement(CERRAR_RESERVA)) {
					ps.setInt(1, s.getConsecutivoSalida());
					ps.setInt(2, s.getConsecutivoReserva());
					ps.executeUpdate();
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
	public List<Salida> mostrarSalidas() {
		return consultar(MOSTRAR, null);
	}

	@Override
	public Salida buscarSalida(int consecutivoSalida) {
		List<Salida> lista = consultar(BUSCAR, consecutivoSalida);
		return lista.isEmpty() ? null : lista.get(0);
	}

	// ----------------------------------------------------------------- apoyo

	private void insertarYEntregar(Connection conexion, Salida s) throws SQLException {
		try (PreparedStatement ps = conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, s.getMotivo());
			JdbcUtil.asignarTexto(ps, 2, s.getObservaciones());
			ps.setDate(3, Date.valueOf(s.getFecha()));
			ps.setString(4, s.getClienteTipoDocumento());
			ps.setString(5, s.getClienteNumeroDocumento());
			ps.setString(6, s.getEmpleadoTipoDocumento());
			ps.setString(7, s.getEmpleadoNumeroDocumento());
			ps.executeUpdate();
			try (ResultSet llaves = ps.getGeneratedKeys()) {
				llaves.next();
				s.setConsecutivoSalida(llaves.getInt(1));
			}
		}
		for (int idEjemplar : s.getEjemplares()) {
			try (PreparedStatement ps = conexion.prepareStatement(ENTREGAR)) {
				ps.setInt(1, s.getConsecutivoSalida());
				ps.setInt(2, idEjemplar);
				ps.executeUpdate();
			}
		}
	}

	/** Bloquea los ejemplares, siempre en el mismo orden, y confirma su estado. */
	private boolean ejemplaresEnEstado(Connection conexion, List<Integer> ejemplares, String estado) throws SQLException {
		for (int idEjemplar : new TreeSet<>(ejemplares)) {
			try (PreparedStatement ps = conexion.prepareStatement(BLOQUEAR_EJEMPLAR)) {
				ps.setInt(1, idEjemplar);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next() || !estado.equals(rs.getString(1))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean reservaReclamable(Connection conexion, Salida s) throws SQLException {
		try (PreparedStatement ps = conexion.prepareStatement(BLOQUEAR_RESERVA)) {
			ps.setInt(1, s.getConsecutivoReserva());
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next() || !"ACTIVA".equals(rs.getString("estado"))) {
					return false;
				}
				Date vence = rs.getDate("fecha_vencimiento");
				return vence == null || !vence.toLocalDate().isBefore(s.getFecha());
			}
		}
	}

	private Set<Integer> ejemplaresDeReserva(Connection conexion, int consecutivo) throws SQLException {
		Set<Integer> ejemplares = new HashSet<>();
		try (PreparedStatement ps = conexion.prepareStatement(EJEMPLARES_DE_RESERVA)) {
			ps.setInt(1, consecutivo);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ejemplares.add(rs.getInt(1));
				}
			}
		}
		return ejemplares;
	}

	private List<Salida> consultar(String sql, Integer parametro) {
		List<Salida> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			if (parametro != null) {
				ps.setInt(1, parametro);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Salida(rs.getInt("consecutivo_salida"), rs.getString("motivo"),
							rs.getString("observaciones"), JdbcUtil.leerFecha(rs, "fecha"),
							rs.getString("cliente_tipo_documento"), rs.getString("cliente_numero_documento"),
							rs.getString("empleado_tipo_documento"), rs.getString("empleado_numero_documento"), null,
							JdbcUtil.leerEntero(rs, "consecutivo_reserva")));
				}
			}
			cargarEjemplares(conexion, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	private void cargarEjemplares(Connection conexion, List<Salida> salidas) throws SQLException {
		if (salidas.isEmpty()) {
			return;
		}
		Map<Integer, Salida> porConsecutivo = new HashMap<>();
		for (Salida s : salidas) {
			porConsecutivo.put(s.getConsecutivoSalida(), s);
		}
		try (PreparedStatement ps = conexion.prepareStatement(TODOS_LOS_RETIROS);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Salida s = porConsecutivo.get(rs.getInt(1));
				if (s != null) {
					s.getEjemplares().add(rs.getInt(2));
				}
			}
		}
	}

}
