package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.RolController;
import co.edu.unbosque.dto.RolDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/roles
 *
 * GET    /api/roles          lista
 * GET    /api/roles/{id}     busca uno
 */
public class RolHandler extends ManejadorBase {

	private RolController rolController;

	public RolHandler() {
		rolController = new RolController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		String codigoRol = subruta;

		if ("GET".equals(metodo) && codigoRol.isEmpty()) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, codigoRol);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (RolDTO dto : rolController.mostrarRoles()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, String codigoRol) throws IOException {
		RolDTO dto = rolController.buscarRol(codigoRol);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el rol " + codigoRol);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private Map<String, Object> aMapa(RolDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("codigoRol", dto.codigoRol());
		mapa.put("nombre", dto.nombre());
		return mapa;
	}
}
