package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import co.edu.unbosque.dao.PrecioReferenciaDAO;
import co.edu.unbosque.model.PrecioReferencia;
import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JdbcUtil;

public class PrecioReferenciaDAOImpl implements PrecioReferenciaDAO {

	private static final String COLUMNAS = "impresion_carta_id_carta, impresion_numero_impresion, fecha, valor, fuente_cotizacion";
	private static final String DE_IMPRESION = " FROM precio_referencia WHERE impresion_carta_id_carta = ? AND impresion_numero_impresion = ?";

	private static final String INSERTAR = "INSERT INTO precio_referencia (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?)";
	private static final String HISTORIAL = "SELECT " + COLUMNAS + DE_IMPRESION + " ORDER BY fecha DESC";
	private static final String BUSCAR = "SELECT " + COLUMNAS + DE_IMPRESION + " AND fecha = ?";
	private static final String VIGENTE = "SELECT " + COLUMNAS + DE_IMPRESION + " ORDER BY fecha DESC LIMIT 1";

	@Override
	public boolean registrarPrecio(PrecioReferencia p) {
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(INSERTAR)) {
			ps.setInt(1, p.getIdCarta());
			ps.setInt(2, p.getNumeroImpresion());
			JdbcUtil.asignarFecha(ps, 3, p.getFecha());
			ps.setBigDecimal(4, p.getValor());
			JdbcUtil.asignarTexto(ps, 5, p.getFuenteCotizacion());
			return ps.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			return false; // ya hay precio ese día, o la impresión no existe
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<PrecioReferencia> mostrarHistorial(int idCarta, int numeroImpresion) {
		return consultar(HISTORIAL, idCarta, numeroImpresion, null);
	}

	@Override
	public PrecioReferencia buscarPrecio(int idCarta, int numeroImpresion, LocalDate fecha) {
		List<PrecioReferencia> lista = consultar(BUSCAR, idCarta, numeroImpresion, fecha);
		return lista.isEmpty() ? null : lista.get(0);
	}

	@Override
	public PrecioReferencia buscarPrecioVigente(int idCarta, int numeroImpresion) {
		List<PrecioReferencia> lista = consultar(VIGENTE, idCarta, numeroImpresion, null);
		return lista.isEmpty() ? null : lista.get(0);
	}

	private List<PrecioReferencia> consultar(String sql, int idCarta, int numeroImpresion, LocalDate fecha) {
		List<PrecioReferencia> lista = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, idCarta);
			ps.setInt(2, numeroImpresion);
			if (fecha != null) {
				ps.setDate(3, Date.valueOf(fecha));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new PrecioReferencia(rs.getInt("impresion_carta_id_carta"),
							rs.getInt("impresion_numero_impresion"), JdbcUtil.leerFecha(rs, "fecha"),
							rs.getBigDecimal("valor"), rs.getString("fuente_cotizacion")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
