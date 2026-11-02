package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.RarezaController;
import co.edu.unbosque.dto.RarezaDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/rarezas
 *
 * GET    /api/rarezas          lista
 * GET    /api/rarezas/{id}     busca uno
 * POST   /api/rarezas          crea
 * PUT    /api/rarezas/{id}     actualiza
 */
public class RarezaHandler extends ManejadorBase {

	private RarezaController rarezaController;

	public RarezaHandler() {
		rarezaController = new RarezaController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idRareza = null;
		if (!subruta.isEmpty()) {
			idRareza = convertirId(subruta);
			if (idRareza == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idRareza == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idRareza);
		} else if ("POST".equals(metodo) && idRareza == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idRareza != null) {
			actualizar(intercambio, idRareza);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (RarezaDTO dto : rarezaController.mostrarRarezas()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idRareza) throws IOException {
		RarezaDTO dto = rarezaController.buscarRareza(idRareza);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la rareza " + idRareza);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		RarezaDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera codigo, nombre");
			return;
		}
		RarezaDTO creado = rarezaController.agregarRareza(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idRareza) throws IOException {
		RarezaDTO dto = desdeJson(leerCuerpo(intercambio), idRareza);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (rarezaController.buscarRareza(idRareza) == null) {
			enviarError(intercambio, 404, "No existe la rareza " + idRareza);
			return;
		}
		if (!rarezaController.actualizarRareza(dto, idRareza)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idRareza + " actualizado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private RarezaDTO desdeJson(String cuerpo, int idRareza) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String codigo = leerTexto(json, "codigo");
		String nombre = leerTexto(json, "nombre");
		if (codigo == null || nombre == null) {
			return null;
		}
		return new RarezaDTO(idRareza, codigo, nombre);
	}

	private Map<String, Object> aMapa(RarezaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idRareza", dto.idRareza());
		mapa.put("codigo", dto.codigo());
		mapa.put("nombre", dto.nombre());
		return mapa;
	}
}
