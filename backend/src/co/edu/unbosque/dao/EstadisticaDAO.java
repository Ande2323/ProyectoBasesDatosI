package co.edu.unbosque.dao;

import java.util.List;
import java.util.Map;

/**
 * Métricas de la sección J: se calculan con consultas sobre los datos base y no se
 * guardan en ninguna tabla. Cada fila es un mapa columna -> valor.
 */
public interface EstadisticaDAO {

	public Map<String, Object> valorInventario();

	public List<Map<String, Object>> ejemplaresPorEstado();

	public List<Map<String, Object>> disponibilidadPorImpresion();

	public List<Map<String, Object>> rotacionPorEdicion();

	public List<Map<String, Object>> reservasVencidasPorCliente();

	public List<Map<String, Object>> salidasPorEmpleado();

	public List<Map<String, Object>> reedicionesPorCarta();

}
