package co.edu.unbosque.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.unbosque.dao.EstadisticaDAO;
import co.edu.unbosque.util.ConexionBD;

public class EstadisticaDAOImpl implements EstadisticaDAO {

	private static final String VALOR_INVENTARIO = "SELECT COUNT(*), COALESCE(SUM(precio_sugerido), 0) FROM ejemplar "
			+ "WHERE estado IN ('DISPONIBLE', 'RESERVADO')";
	private static final String POR_ESTADO = "SELECT estado, COUNT(*) FROM ejemplar GROUP BY estado ORDER BY estado";
	private static final String DISPONIBILIDAD = "SELECT c.id_carta, c.nombre_oficial, e.impresion_numero_impresion, "
			+ "SUM(CASE WHEN e.estado = 'DISPONIBLE' THEN 1 ELSE 0 END), "
			+ "SUM(CASE WHEN e.estado = 'RESERVADO' THEN 1 ELSE 0 END), "
			+ "SUM(CASE WHEN e.estado = 'VENDIDO' THEN 1 ELSE 0 END) "
			+ "FROM ejemplar e JOIN carta c ON c.id_carta = e.impresion_carta_id_carta "
			+ "GROUP BY c.id_carta, c.nombre_oficial, e.impresion_numero_impresion "
			+ "ORDER BY c.nombre_oficial, e.impresion_numero_impresion";
	private static final String ROTACION = "SELECT ed.id_edicion, ed.nombre, COUNT(e.id_ejemplar), "
			+ "SUM(CASE WHEN e.salida_consecutivo_salida IS NOT NULL THEN 1 ELSE 0 END) "
			+ "FROM edicion ed JOIN impresion i ON i.edicion_id_edicion = ed.id_edicion "
			+ "JOIN ejemplar e ON e.impresion_carta_id_carta = i.carta_id_carta "
			+ "AND e.impresion_numero_impresion = i.numero_impresion "
			+ "GROUP BY ed.id_edicion, ed.nombre ORDER BY ed.nombre";
	private static final String VENCIDAS = "SELECT cl.tipo_documento, cl.numero_documento, cl.nombres, cl.apellidos, COUNT(*) "
			+ "FROM reserva r JOIN cliente cl ON cl.tipo_documento = r.cliente_tipo_documento "
			+ "AND cl.numero_documento = r.cliente_numero_documento WHERE r.estado = 'VENCIDA' "
			+ "GROUP BY cl.tipo_documento, cl.numero_documento, cl.nombres, cl.apellidos ORDER BY COUNT(*) DESC";
	private static final String POR_EMPLEADO = "SELECT em.tipo_documento, em.numero_documento, em.nombres, em.apellidos, "
			+ "COUNT(s.consecutivo_salida) FROM empleado em LEFT JOIN salida s "
			+ "ON s.empleado_tipo_documento = em.tipo_documento AND s.empleado_numero_documento = em.numero_documento "
			+ "GROUP BY em.tipo_documento, em.numero_documento, em.nombres, em.apellidos ORDER BY COUNT(s.consecutivo_salida) DESC";
	private static final String REEDICIONES = "SELECT c.id_carta, c.nombre_oficial, COUNT(*) FROM impresion i "
			+ "JOIN carta c ON c.id_carta = i.carta_id_carta WHERE i.impresion_numero_impresion IS NOT NULL "
			+ "GROUP BY c.id_carta, c.nombre_oficial ORDER BY COUNT(*) DESC";

	@Override
	public Map<String, Object> valorInventario() {
		List<Map<String, Object>> filas = consultar(VALOR_INVENTARIO, "ejemplares", "valorTotal");
		return filas.isEmpty() ? new LinkedHashMap<>() : filas.get(0);
	}

	@Override
	public List<Map<String, Object>> ejemplaresPorEstado() {
		return consultar(POR_ESTADO, "estado", "cantidad");
	}

	@Override
	public List<Map<String, Object>> disponibilidadPorImpresion() {
		return consultar(DISPONIBILIDAD, "idCarta", "nombreOficial", "numeroImpresion", "disponibles", "reservados", "vendidos");
	}

	@Override
	public List<Map<String, Object>> rotacionPorEdicion() {
		return consultar(ROTACION, "idEdicion", "nombre", "ejemplares", "salidos");
	}

	@Override
	public List<Map<String, Object>> reservasVencidasPorCliente() {
		return consultar(VENCIDAS, "tipoDocumento", "numeroDocumento", "nombres", "apellidos", "reservasVencidas");
	}

	@Override
	public List<Map<String, Object>> salidasPorEmpleado() {
		return consultar(POR_EMPLEADO, "tipoDocumento", "numeroDocumento", "nombres", "apellidos", "salidas");
	}

	@Override
	public List<Map<String, Object>> reedicionesPorCarta() {
		return consultar(REEDICIONES, "idCarta", "nombreOficial", "reediciones");
	}

	/** Los nombres se dan por posición, para no depender de cómo cada motor escribe los alias. */
	private List<Map<String, Object>> consultar(String sql, String... claves) {
		List<Map<String, Object>> filas = new ArrayList<>();
		try (Connection conexion = ConexionBD.obtenerConexion();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Map<String, Object> fila = new LinkedHashMap<>();
				for (int i = 0; i < claves.length; i++) {
					fila.put(claves[i], rs.getObject(i + 1));
				}
				filas.add(fila);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return filas;
	}

}
