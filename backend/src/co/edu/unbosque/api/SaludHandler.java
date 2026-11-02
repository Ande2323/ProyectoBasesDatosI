package co.edu.unbosque.api;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.util.ConexionBD;
import co.edu.unbosque.util.JsonUtil;

/** GET /api/salud — confirma que el servidor responde y si llega a MySQL. */
public class SaludHandler extends ManejadorBase {

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return PUBLICO;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		if (!"GET".equals(intercambio.getRequestMethod())) {
			metodoNoPermitido(intercambio);
			return;
		}
		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("servidor", "activo");
		respuesta.put("baseDatos", ConexionBD.probarConexion() ? "conectada" : "sin conexión");
		enviarJson(intercambio, 200, JsonUtil.aJson(respuesta));
	}
}
