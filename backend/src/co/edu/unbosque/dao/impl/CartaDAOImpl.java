package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unbosque.dao.CartaDAO;
import co.edu.unbosque.model.Carta;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

/**
 * La carta y sus filas en carta_color y carta_tipo se escriben en una sola
 * transacción: si algo falla (por ejemplo un color que no existe), no queda nada
 * a medias.
 */
public class CartaDAOImpl implements CartaDAO {

	private static final String COLUMNAS = "id_carta, nombre_oficial, texto_oraculo, coste_mana, poder, resistencia, lealtad";

	private static final String INSERTAR = "INSERT INTO carta (nombre_oficial, texto_oraculo, coste_mana, poder, resistencia, lealtad) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE carta SET nombre_oficial = ?, texto_oraculo = ?, coste_mana = ?, poder = ?, resistencia = ?, lealtad = ? WHERE id_carta = ?";
	private static final String ELIMINAR = "DELETE FROM carta WHERE id_carta = ?";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM carta ORDER BY nombre_oficial";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM carta WHERE id_carta = ?";
	private static final String BUSCAR_NOMBRE = "SELECT " + COLUMNAS
			+ " FROM carta WHERE UPPER(nombre_oficial) LIKE UPPER(?) ESCAPE '!' ORDER BY nombre_oficial";

	private static final String INSERTAR_COLOR = "INSERT INTO carta_color (carta_id_carta, color_id_color) VALUES (?, ?)";
	private static final String INSERTAR_TIPO = "INSERT INTO carta_tipo (carta_id_carta, tipo_id_tipo) VALUES (?, ?)";
	private static final String BORRAR_COLORES = "DELETE FROM carta_color WHERE carta_id_carta = ?";
	private static final String BORRAR_TIPOS = "DELETE FROM carta_tipo WHERE carta_id_carta = ?";
	private static final String TODOS_LOS_COLORES = "SELECT cc.carta_id_carta, cc.color_id_color FROM carta_color cc "
			+ "JOIN color c ON c.id_color = cc.color_id_color ORDER BY cc.carta_id_carta, c.orden_canonico";
	private static final String TODOS_LOS_TIPOS = "SELECT carta_id_carta, tipo_id_tipo FROM carta_tipo ORDER BY carta_id_carta, tipo_id_tipo";

	@Override
	public boolean crearCarta(Carta c) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				try (PreparedStatement ps = conexion.prepareStatement(INSERTAR, new String[] { "id_carta" })) {
					asignarDatos(ps, c);
					ps.executeUpdate();
					try (ResultSet llaves = ps.getGeneratedKeys()) {
						llaves.next();
						c.setIdCarta(llaves.getInt(1));
					}
				}
				guardarRelaciones(conexion, c);
				conexion.commit();
				return true;
			} catch (SQLException e) {
				conexion.rollback();
				reportar(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean actualizarCarta(Carta c, int idCarta) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				try (PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
					asignarDatos(ps, c);
					ps.setInt(7, idCarta);
					if (ps.executeUpdate() != 1) {
						conexion.rollback();
						return false;
					}
				}
				c.setIdCarta(idCarta);
				borrarRelaciones(conexion, idCarta);
				guardarRelaciones(conexion, c);
				conexion.commit();
				return true;
			} catch (SQLException e) {
				conexion.rollback();
				reportar(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Si la carta ya tiene impresiones, MySQL impide borrarla y se deshace todo. */
	@Override
	public boolean eliminarCarta(int idCarta) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				borrarRelaciones(conexion, idCarta);
				try (PreparedStatement ps = conexion.prepareStatement(ELIMINAR)) {
					ps.setInt(1, idCarta);
					if (ps.executeUpdate() != 1) {
						conexion.rollback();
						return false;
					}
				}
				conexion.commit();
				return true;
			} catch (SQLException e) {
				conexion.rollback();
				reportar(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Carta> mostrarCartas() {
		return consultar(MOSTRAR, null);
	}

	/** Busca por parte del nombre, sin importar mayúsculas: "bolt" encuentra "Lightning Bolt". */
	@Override
	public List<Carta> buscarCartasPorNombre(String texto) {
		String patron = "%" + texto.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
		return consultar(BUSCAR_NOMBRE, patron);
	}

	@Override
	public Carta buscarCarta(int idCarta) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR)) {
			ps.setInt(1, idCarta);
			List<Carta> lista = new ArrayList<>();
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					lista.add(mapearFila(rs));
				}
			}
			if (lista.isEmpty()) {
				return null;
			}
			cargarRelaciones(conexion, lista);
			return lista.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ----------------------------------------------------------------- apoyo

	private List<Carta> consultar(String sql, String parametro) {
		List<Carta> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			if (parametro != null) {
				ps.setString(1, parametro);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearFila(rs));
				}
			}
			cargarRelaciones(conexion, lista);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	private void asignarDatos(PreparedStatement ps, Carta c) throws SQLException {
		ps.setString(1, c.getNombreOficial());
		JdbcUtil.asignarTexto(ps, 2, c.getTextoOraculo());
		JdbcUtil.asignarTexto(ps, 3, c.getCosteMana());
		JdbcUtil.asignarTexto(ps, 4, c.getPoder());
		JdbcUtil.asignarTexto(ps, 5, c.getResistencia());
		JdbcUtil.asignarTexto(ps, 6, c.getLealtad());
	}

	private void guardarRelaciones(Connection conexion, Carta c) throws SQLException {
		try (PreparedStatement ps = conexion.prepareStatement(INSERTAR_COLOR)) {
			for (Integer idColor : c.getColores()) {
				ps.setInt(1, c.getIdCarta());
				ps.setInt(2, idColor);
				ps.addBatch();
			}
			ps.executeBatch();
		}
		try (PreparedStatement ps = conexion.prepareStatement(INSERTAR_TIPO)) {
			for (Integer idTipo : c.getTipos()) {
				ps.setInt(1, c.getIdCarta());
				ps.setInt(2, idTipo);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private void borrarRelaciones(Connection conexion, int idCarta) throws SQLException {
		for (String sql : new String[] { BORRAR_COLORES, BORRAR_TIPOS }) {
			try (PreparedStatement ps = conexion.prepareStatement(sql)) {
				ps.setInt(1, idCarta);
				ps.executeUpdate();
			}
		}
	}

	/** Llena colores y tipos con dos consultas en total, no dos por cada carta. */
	private void cargarRelaciones(Connection conexion, List<Carta> cartas) throws SQLException {
		if (cartas.isEmpty()) {
			return;
		}
		Map<Integer, Carta> porId = new HashMap<>();
		for (Carta c : cartas) {
			porId.put(c.getIdCarta(), c);
		}
		try (PreparedStatement ps = conexion.prepareStatement(TODOS_LOS_COLORES);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Carta c = porId.get(rs.getInt("carta_id_carta"));
				if (c != null) {
					c.getColores().add(rs.getInt("color_id_color"));
				}
			}
		}
		try (PreparedStatement ps = conexion.prepareStatement(TODOS_LOS_TIPOS);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Carta c = porId.get(rs.getInt("carta_id_carta"));
				if (c != null) {
					c.getTipos().add(rs.getInt("tipo_id_tipo"));
				}
			}
		}
	}

	private Carta mapearFila(ResultSet rs) throws SQLException {
		return new Carta(rs.getInt("id_carta"), rs.getString("nombre_oficial"), rs.getString("texto_oraculo"),
				rs.getString("coste_mana"), rs.getString("poder"), rs.getString("resistencia"),
				rs.getString("lealtad"), null, null);
	}

	/** Las violaciones de restricciones son esperables (nombre repetido, en uso); el resto se imprime. */
	private void reportar(SQLException e) {
		String estado = e.getSQLState();
		if (estado == null || !estado.startsWith("23")) {
			e.printStackTrace();
		}
	}

}
