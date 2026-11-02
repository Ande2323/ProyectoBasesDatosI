package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import co.edu.unbosque.dao.ReservaDAO;
import co.edu.unbosque.model.Reserva;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class ReservaDAOImpl implements ReservaDAO {

	private static final String COLUMNAS = "consecutivo, fecha_creacion, estado, fecha_vencimiento, cliente_tipo_documento, "
			+ "cliente_numero_documento, salida_consecutivo_salida";

	private static final String BLOQUEAR_EJEMPLAR = "SELECT estado FROM ejemplar WHERE id_ejemplar = ? FOR UPDATE";
	private static final String BLOQUEAR_RESERVA = "SELECT estado FROM reserva WHERE consecutivo = ? FOR UPDATE";
	private static final String INSERTAR = "INSERT INTO reserva (fecha_creacion, estado, fecha_vencimiento, "
			+ "cliente_tipo_documento, cliente_numero_documento) VALUES (?, ?, ?, ?, ?)";
	private static final String APARTAR = "INSERT INTO ejemplar_reserva (id_ejemplar, consecutivo) VALUES (?, ?)";
	private static final String MARCAR_RESERVADO = "UPDATE ejemplar SET estado = 'RESERVADO' WHERE id_ejemplar = ?";
	private static final String LIBERAR_EJEMPLARES = "UPDATE ejemplar SET estado = 'DISPONIBLE' WHERE estado = 'RESERVADO' "
			+ "AND id_ejemplar IN (SELECT id_ejemplar FROM ejemplar_reserva WHERE consecutivo = ?)";
	private static final String CAMBIAR_ESTADO = "UPDATE reserva SET estado = ? WHERE consecutivo = ?";
	private static final String LIBERAR_VENCIDOS = "UPDATE ejemplar SET estado = 'DISPONIBLE' WHERE estado = 'RESERVADO' "
			+ "AND id_ejemplar IN (SELECT er.id_ejemplar FROM ejemplar_reserva er JOIN reserva r "
			+ "ON r.consecutivo = er.consecutivo WHERE r.estado = 'ACTIVA' AND r.fecha_vencimiento < ?)";
	private static final String VENCER = "UPDATE reserva SET estado = 'VENCIDA' WHERE estado = 'ACTIVA' AND fecha_vencimiento < ?";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM reserva ORDER BY consecutivo DESC";
	private static final String MOSTRAR_ESTADO = "SELECT " + COLUMNAS + " FROM reserva WHERE estado = ? ORDER BY consecutivo DESC";
	private static final String MOSTRAR_CLIENTE = "SELECT " + COLUMNAS + " FROM reserva WHERE cliente_tipo_documento = ? "
			+ "AND cliente_numero_documento = ? ORDER BY consecutivo DESC";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM reserva WHERE consecutivo = ?";
	private static final String TODOS_LOS_APARTADOS = "SELECT consecutivo, id_ejemplar FROM ejemplar_reserva ORDER BY consecutivo, id_ejemplar";

	@Override
	public boolean crearReserva(Reserva r) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				// Siempre en el mismo orden, para que dos reservas simultáneas no se bloqueen entre sí.
				for (int idEjemplar : new TreeSet<>(r.getEjemplares())) {
					if (!"DISPONIBLE".equals(bloquearEjemplar(conexion, idEjemplar))) {
						conexion.rollback();
						return false;
					}
				}
				try (PreparedStatement ps = conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
					JdbcUtil.asignarFecha(ps, 1, r.getFechaCreacion());
					ps.setString(2, r.getEstado());
					JdbcUtil.asignarFecha(ps, 3, r.getFechaVencimiento());
					ps.setString(4, r.getClienteTipoDocumento());
					ps.setString(5, r.getClienteNumeroDocumento());
					ps.executeUpdate();
					try (ResultSet llaves = ps.getGeneratedKeys()) {
						llaves.next();
						r.setConsecutivo(llaves.getInt(1));
					}
				}
				for (int idEjemplar : r.getEjemplares()) {
					try (PreparedStatement ps = conexion.prepareStatement(APARTAR)) {
						ps.setInt(1, idEjemplar);
						ps.setInt(2, r.getConsecutivo());
						ps.executeUpdate();
					}
					try (PreparedStatement ps = conexion.prepareStatement(MARCAR_RESERVADO)) {
						ps.setInt(1, idEjemplar);
						ps.executeUpdate();
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
	public boolean cancelarReserva(int consecutivo) {
		return cerrarYLiberar(consecutivo, "CANCELADA");
	}

	@Override
	public boolean marcarVencida(int consecutivo) {
		return cerrarYLiberar(consecutivo, "VENCIDA");
	}

	@Override
	public void vencerReservas(LocalDate hoy) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				// Primero los ejemplares: la consulta necesita ver las reservas aún ACTIVAS.
				for (String sql : new String[] { LIBERAR_VENCIDOS, VENCER }) {
					try (PreparedStatement ps = conexion.prepareStatement(sql)) {
						ps.setDate(1, Date.valueOf(hoy));
						ps.executeUpdate();
					}
				}
				conexion.commit();
			} catch (SQLException e) {
				conexion.rollback();
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Reserva> mostrarReservas(String estado) {
		return estado == null ? consultar(MOSTRAR) : consultar(MOSTRAR_ESTADO, estado);
	}

	@Override
	public List<Reserva> mostrarReservasDeCliente(String tipoDocumento, String numeroDocumento) {
		return consultar(MOSTRAR_CLIENTE, tipoDocumento, numeroDocumento);
	}

	@Override
	public Reserva buscarReserva(int consecutivo) {
		List<Reserva> lista = consultar(BUSCAR, String.valueOf(consecutivo));
		return lista.isEmpty() ? null : lista.get(0);
	}

	// ----------------------------------------------------------------- apoyo

	private boolean cerrarYLiberar(int consecutivo, String nuevoEstado) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				try (PreparedStatement ps = conexion.prepareStatement(BLOQUEAR_RESERVA)) {
					ps.setInt(1, consecutivo);
					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next() || !"ACTIVA".equals(rs.getString(1))) {
							conexion.rollback();
							return false;
						}
					}
				}
				try (PreparedStatement ps = conexion.prepareStatement(LIBERAR_EJEMPLARES)) {
					ps.setInt(1, consecutivo);
					ps.executeUpdate();
				}
				try (PreparedStatement ps = conexion.prepareStatement(CAMBIAR_ESTADO)) {
					ps.setString(1, nuevoEstado);
					ps.setInt(2, consecutivo);
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

	/** Retorna el estado del ejemplar con la fila bloqueada, o null si no existe. */
	private String bloquearEjemplar(Connection conexion, int idEjemplar) throws SQLException {
		try (PreparedStatement ps = conexion.prepareStatement(BLOQUEAR_EJEMPLAR)) {
			ps.setInt(1, idEjemplar);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getString(1) : null;
			}
		}
	}

	private List<Reserva> consultar(String sql, String... parametros) {
		List<Reserva> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int i = 0; i < parametros.length; i++) {
				ps.setString(i + 1, parametros[i]);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new Reserva(rs.getInt("consecutivo"), JdbcUtil.leerFecha(rs, "fecha_creacion"),
							rs.getString("estado"), JdbcUtil.leerFecha(rs, "fecha_vencimiento"),
							rs.getString("cliente_tipo_documento"), rs.getString("cliente_numero_documento"),
							JdbcUtil.leerEntero(rs, "salida_consecutivo_salida"), null));
				}
			}
			cargarEjemplares(conexion, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	private void cargarEjemplares(Connection conexion, List<Reserva> reservas) throws SQLException {
		if (reservas.isEmpty()) {
			return;
		}
		Map<Integer, Reserva> porConsecutivo = new HashMap<>();
		for (Reserva r : reservas) {
			porConsecutivo.put(r.getConsecutivo(), r);
		}
		try (PreparedStatement ps = conexion.prepareStatement(TODOS_LOS_APARTADOS);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Reserva r = porConsecutivo.get(rs.getInt("consecutivo"));
				if (r != null) {
					r.getEjemplares().add(rs.getInt("id_ejemplar"));
				}
			}
		}
	}

}
