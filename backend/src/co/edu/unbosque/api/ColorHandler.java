package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.ColorController;
import co.edu.unbosque.dto.ColorDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/colores
 *
 * GET    /api/colores          lista
 * GET    /api/colores/{id}     busca uno
 * POST   /api/colores          crea
 * PUT    /api/colores/{id}     actualiza
 * DELETE /api/colores/{id}     elimina (solo si nadie lo usa)
 */
public class ColorHandler extends ManejadorBase {

	private ColorController colorController;

	public ColorHandler() {
		colorController = new ColorController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idColor = null;
		if (!subruta.isEmpty()) {
			idColor = convertirId(subruta);
			if (idColor == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idColor == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idColor);
		} else if ("POST".equals(metodo) && idColor == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idColor != null) {
			actualizar(intercambio, idColor);
		} else if ("DELETE".equals(metodo) && idColor != null) {
			eliminar(intercambio, idColor);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (ColorDTO dto : colorController.mostrarColores()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idColor) throws IOException {
		ColorDTO dto = colorController.buscarColor(idColor);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el color " + idColor);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		ColorDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera nombreColor, ordenCanonico");
			return;
		}
		ColorDTO creado = colorController.agregarColor(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idColor) throws IOException {
		ColorDTO dto = desdeJson(leerCuerpo(intercambio), idColor);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (colorController.buscarColor(idColor) == null) {
			enviarError(intercambio, 404, "No existe el color " + idColor);
			return;
		}
		if (!colorController.actualizarColor(dto, idColor)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idColor + " actualizado");
	}

	private void eliminar(HttpExchange intercambio, Integer idColor) throws IOException {
		if (colorController.buscarColor(idColor) == null) {
			enviarError(intercambio, 404, "No existe el color " + idColor);
			return;
		}
		if (!colorController.eliminarColor(idColor)) {
			enviarError(intercambio, 409, "No se puede eliminar: hay registros que lo usan");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idColor + " eliminado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private ColorDTO desdeJson(String cuerpo, int idColor) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String nombreColor = leerTexto(json, "nombreColor");
		Integer ordenCanonico = leerEntero(json, "ordenCanonico");
		if (json.get("ordenCanonico") != null && ordenCanonico == null) {
			return null; // vino pero no es entero
		}
		if (nombreColor == null) {
			return null;
		}
		return new ColorDTO(idColor, nombreColor, ordenCanonico);
	}

	private Map<String, Object> aMapa(ColorDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idColor", dto.idColor());
		mapa.put("nombreColor", dto.nombreColor());
		mapa.put("ordenCanonico", dto.ordenCanonico());
		return mapa;
	}
}
