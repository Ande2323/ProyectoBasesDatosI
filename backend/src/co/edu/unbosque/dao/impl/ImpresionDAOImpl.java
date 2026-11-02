package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.ImpresionDAO;
import co.edu.unbosque.model.Impresion;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class ImpresionDAOImpl implements ImpresionDAO {

	private static final String COLUMNAS = "carta_id_carta, numero_impresion, numero_coleccion, indicador_foil, edicion_id_edicion, "
			+ "idioma_id_idioma, rareza_id_rareza, artista_id_artista, impresion_carta_id_carta, impresion_numero_impresion";

	/** Bloquea la fila de la carta para que dos personas no saquen el mismo número a la vez. */
	private static final String BLOQUEAR_CARTA = "SELECT id_carta FROM carta WHERE id_carta = ? FOR UPDATE";
	private static final String SIGUIENTE_NUMERO = "SELECT COALESCE(MAX(numero_impresion), 0) + 1 FROM impresion WHERE carta_id_carta = ?";
	private static final String INSERTAR = "INSERT INTO impresion (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTUALIZAR = "UPDATE impresion SET numero_coleccion = ?, indicador_foil = ?, edicion_id_edicion = ?, "
			+ "idioma_id_idioma = ?, rareza_id_rareza = ?, artista_id_artista = ?, impresion_carta_id_carta = ?, "
			+ "impresion_numero_impresion = ? WHERE carta_id_carta = ? AND numero_impresion = ?";
	private static final String ELIMINAR = "DELETE FROM impresion WHERE carta_id_carta = ? AND numero_impresion = ?";
	private static final String MOSTRAR = "SELECT " + COLUMNAS + " FROM impresion ORDER BY carta_id_carta, numero_impresion";
	private static final String MOSTRAR_DE_CARTA = "SELECT " + COLUMNAS
			+ " FROM impresion WHERE carta_id_carta = ? ORDER BY numero_impresion";
	private static final String MOSTRAR_REIMPRESIONES = "SELECT " + COLUMNAS
			+ " FROM impresion WHERE impresion_carta_id_carta = ? AND impresion_numero_impresion = ? ORDER BY numero_impresion";
	private static final String BUSCAR = "SELECT " + COLUMNAS + " FROM impresion WHERE carta_id_carta = ? AND numero_impresion = ?";
	private static final String BUSCAR_FISICA = "SELECT " + COLUMNAS + " FROM impresion WHERE edicion_id_edicion = ? "
			+ "AND idioma_id_idioma = ? AND numero_coleccion = ? AND indicador_foil = ?";

	/** Asigna el siguiente número de impresión de la carta y lo deja en i. */
	@Override
	public boolean crearImpresion(Impresion i) {
		try (Connection conexion = ConexionBD.obtenerConexion()) {
			conexion.setAutoCommit(false);
			try {
				try (PreparedStatement ps = conexion.prepareStatement(BLOQUEAR_CARTA)) {
					ps.setInt(1, i.getIdCarta());
					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next()) {
							conexion.rollback();
							return false; // la carta no existe
						}
					}
				}
				try (PreparedStatement ps = conexion.prepareStatement(SIGUIENTE_NUMERO)) {
					ps.setInt(1, i.getIdCarta());
					try (ResultSet rs = ps.executeQuery()) {
						rs.next();
						i.setNumeroImpresion(rs.getInt(1));
					}
				}
				try (PreparedStatement ps = conexion.prepareStatement(INSERTAR)) {
					ps.setInt(1, i.getIdCarta());
					ps.setInt(2, i.getNumeroImpresion());
					asignarDatos(ps, i, 3);
					ps.executeUpdate();
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
	public boolean actualizarImpresion(Impresion i, int idCarta, int numeroImpresion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ACTUALIZAR)) {
			asignarDatos(ps, i, 1);
			ps.setInt(9, idCarta);
			ps.setInt(10, numeroImpresion);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			reportar(e);
		}
		return false;
	}

	/** MySQL impide borrarla si tiene ejemplares, precios o reimpresiones. */
	@Override
	public boolean eliminarImpresion(int idCarta, int numeroImpresion) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(ELIMINAR)) {
			ps.setInt(1, idCarta);
			ps.setInt(2, numeroImpresion);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			reportar(e);
		}
		return false;
	}

	@Override
	public List<Impresion> mostrarImpresiones() {
		return consultar(MOSTRAR);
	}

	@Override
	public List<Impresion> mostrarImpresionesDeCarta(int idCarta) {
		return consultar(MOSTRAR_DE_CARTA, idCarta);
	}

	@Override
	public List<Impresion> mostrarReimpresiones(int idCarta, int numeroImpresion) {
		return consultar(MOSTRAR_REIMPRESIONES, idCarta, numeroImpresion);
	}

	@Override
	public Impresion buscarImpresion(int idCarta, int numeroImpresion) {
		List<Impresion> lista = consultar(BUSCAR, idCarta, numeroImpresion);
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public Impresion buscarImpresionFisica(int idEdicion, int idIdioma, String numeroColeccion, boolean foil) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(BUSCAR_FISICA)) {
			ps.setInt(1, idEdicion);
			ps.setInt(2, idIdioma);
			ps.setString(3, numeroColeccion);
			ps.setString(4, foil ? "S" : "N");
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

	// ----------------------------------------------------------------- apoyo

	private List<Impresion> consultar(String sql, int... parametros) {
		List<Impresion> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int p = 0; p < parametros.length; p++) {
				ps.setInt(p + 1, parametros[p]);
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearFila(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	/** Asigna los 8 datos que no son llave a partir de la posición indicada. */
	private void asignarDatos(PreparedStatement ps, Impresion i, int desde) throws SQLException {
		ps.setString(desde, i.getNumeroColeccion());
		ps.setString(desde + 1, i.isFoil() ? "S" : "N");
		ps.setInt(desde + 2, i.getIdEdicion());
		ps.setInt(desde + 3, i.getIdIdioma());
		ps.setInt(desde + 4, i.getIdRareza());
		ps.setInt(desde + 5, i.getIdArtista());
		JdbcUtil.asignarEntero(ps, desde + 6, i.getOriginalIdCarta());
		JdbcUtil.asignarEntero(ps, desde + 7, i.getOriginalNumeroImpresion());
	}

	private Impresion mapearFila(ResultSet rs) throws SQLException {
		return new Impresion(rs.getInt("carta_id_carta"), rs.getInt("numero_impresion"), rs.getString("numero_coleccion"),
				"S".equals(rs.getString("indicador_foil")), rs.getInt("edicion_id_edicion"), rs.getInt("idioma_id_idioma"),
				rs.getInt("rareza_id_rareza"), rs.getInt("artista_id_artista"), JdbcUtil.leerEntero(rs, "impresion_carta_id_carta"),
				JdbcUtil.leerEntero(rs, "impresion_numero_impresion"));
	}

	private void reportar(SQLException e) {
		if (!(e instanceof SQLIntegrityConstraintViolationException)) {
			e.printStackTrace();
		}
	}

}
