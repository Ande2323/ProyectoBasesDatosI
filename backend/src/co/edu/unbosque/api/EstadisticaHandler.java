package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.EstadisticaController;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/estadisticas (personal). Solo lectura.
 *
 * GET /api/estadisticas           nombres de las métricas disponibles
 * GET /api/estadisticas/{metrica} valor-inventario, ejemplares-por-estado, disponibilidad,
 *                                 rotacion-edicion, reservas-vencidas, salidas-empleado, reediciones
 *
 * La evolución del precio de una impresión está en GET /api/precios.
 */
public class EstadisticaHandler extends ManejadorBase {

	private EstadisticaController estadisticaController;

	public EstadisticaHandler() {
		estadisticaController = new EstadisticaController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return PERSONAL;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		if (!"GET".equals(intercambio.getRequestMethod())) {
			metodoNoPermitido(intercambio);
			return;
		}
		String metrica = obtenerSubruta(intercambio);
		if (metrica.isEmpty()) {
			enviarJson(intercambio, 200, JsonUtil.aJson(new ArrayList<Object>(estadisticaController.metricas())));
			return;
		}
		Object resultado = estadisticaController.consultar(metrica);
		if (resultado == null) {
			enviarError(intercambio, 404, "No existe la métrica " + metrica);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(resultado));
	}
}
