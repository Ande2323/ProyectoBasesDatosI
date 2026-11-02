package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.IdiomaController;
import co.edu.unbosque.dto.IdiomaDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/idiomas
 *
 * GET    /api/idiomas          lista
 * GET    /api/idiomas/{id}     busca uno
 * POST   /api/idiomas          crea
 * PUT    /api/idiomas/{id}     actualiza
 */
public class IdiomaHandler extends ManejadorBase {

	private IdiomaController idiomaController;

	public IdiomaHandler() {
		idiomaController = new IdiomaController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idIdioma = null;
		if (!subruta.isEmpty()) {
			idIdioma = convertirId(subruta);
			if (idIdioma == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idIdioma == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idIdioma);
		} else if ("POST".equals(metodo) && idIdioma == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idIdioma != null) {
			actualizar(intercambio, idIdioma);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (IdiomaDTO dto : idiomaController.mostrarIdiomas()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idIdioma) throws IOException {
		IdiomaDTO dto = idiomaController.buscarIdioma(idIdioma);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el idioma " + idIdioma);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		IdiomaDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera codigo, nombre");
			return;
		}
		IdiomaDTO creado = idiomaController.agregarIdioma(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idIdioma) throws IOException {
		IdiomaDTO dto = desdeJson(leerCuerpo(intercambio), idIdioma);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (idiomaController.buscarIdioma(idIdioma) == null) {
			enviarError(intercambio, 404, "No existe el idioma " + idIdioma);
			return;
		}
		if (!idiomaController.actualizarIdioma(dto, idIdioma)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idIdioma + " actualizado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private IdiomaDTO desdeJson(String cuerpo, int idIdioma) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String codigo = leerTexto(json, "codigo");
		String nombre = leerTexto(json, "nombre");
		if (codigo == null || nombre == null) {
			return null;
		}
		return new IdiomaDTO(idIdioma, codigo, nombre);
	}

	private Map<String, Object> aMapa(IdiomaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idIdioma", dto.idIdioma());
		mapa.put("codigo", dto.codigo());
		mapa.put("nombre", dto.nombre());
		return mapa;
	}
}
