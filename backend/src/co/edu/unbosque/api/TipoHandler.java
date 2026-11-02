package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.TipoController;
import co.edu.unbosque.dto.TipoDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/tipos
 *
 * GET    /api/tipos          lista
 * GET    /api/tipos/{id}     busca uno
 * POST   /api/tipos          crea
 * PUT    /api/tipos/{id}     actualiza
 * DELETE /api/tipos/{id}     elimina (solo si nadie lo usa)
 */
public class TipoHandler extends ManejadorBase {

	private TipoController tipoController;

	public TipoHandler() {
		tipoController = new TipoController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idTipo = null;
		if (!subruta.isEmpty()) {
			idTipo = convertirId(subruta);
			if (idTipo == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idTipo == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idTipo);
		} else if ("POST".equals(metodo) && idTipo == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idTipo != null) {
			actualizar(intercambio, idTipo);
		} else if ("DELETE".equals(metodo) && idTipo != null) {
			eliminar(intercambio, idTipo);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (TipoDTO dto : tipoController.mostrarTipos()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idTipo) throws IOException {
		TipoDTO dto = tipoController.buscarTipo(idTipo);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el tipo " + idTipo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		TipoDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera nivel, codigo, nombre");
			return;
		}
		TipoDTO creado = tipoController.agregarTipo(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idTipo) throws IOException {
		TipoDTO dto = desdeJson(leerCuerpo(intercambio), idTipo);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (tipoController.buscarTipo(idTipo) == null) {
			enviarError(intercambio, 404, "No existe el tipo " + idTipo);
			return;
		}
		if (!tipoController.actualizarTipo(dto, idTipo)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idTipo + " actualizado");
	}

	private void eliminar(HttpExchange intercambio, Integer idTipo) throws IOException {
		if (tipoController.buscarTipo(idTipo) == null) {
			enviarError(intercambio, 404, "No existe el tipo " + idTipo);
			return;
		}
		if (!tipoController.eliminarTipo(idTipo)) {
			enviarError(intercambio, 409, "No se puede eliminar: hay registros que lo usan");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idTipo + " eliminado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private TipoDTO desdeJson(String cuerpo, int idTipo) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		Integer nivel = leerEntero(json, "nivel");
		String codigo = leerTexto(json, "codigo");
		String nombre = leerTexto(json, "nombre");
		if (nivel == null || codigo == null || nombre == null) {
			return null;
		}
		return new TipoDTO(idTipo, nivel, codigo, nombre);
	}

	private Map<String, Object> aMapa(TipoDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idTipo", dto.idTipo());
		mapa.put("nivel", dto.nivel());
		mapa.put("codigo", dto.codigo());
		mapa.put("nombre", dto.nombre());
		return mapa;
	}
}
