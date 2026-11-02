package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.ArtistaController;
import co.edu.unbosque.dto.ArtistaDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/artistas
 *
 * GET    /api/artistas          lista
 * GET    /api/artistas/{id}     busca uno
 * POST   /api/artistas          crea
 * PUT    /api/artistas/{id}     actualiza
 * DELETE /api/artistas/{id}     elimina (solo si nadie lo usa)
 */
public class ArtistaHandler extends ManejadorBase {

	private ArtistaController artistaController;

	public ArtistaHandler() {
		artistaController = new ArtistaController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idArtista = null;
		if (!subruta.isEmpty()) {
			idArtista = convertirId(subruta);
			if (idArtista == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idArtista == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idArtista);
		} else if ("POST".equals(metodo) && idArtista == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idArtista != null) {
			actualizar(intercambio, idArtista);
		} else if ("DELETE".equals(metodo) && idArtista != null) {
			eliminar(intercambio, idArtista);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (ArtistaDTO dto : artistaController.mostrarArtistas()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idArtista) throws IOException {
		ArtistaDTO dto = artistaController.buscarArtista(idArtista);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el artista " + idArtista);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		ArtistaDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera nombre");
			return;
		}
		ArtistaDTO creado = artistaController.agregarArtista(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idArtista) throws IOException {
		ArtistaDTO dto = desdeJson(leerCuerpo(intercambio), idArtista);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (artistaController.buscarArtista(idArtista) == null) {
			enviarError(intercambio, 404, "No existe el artista " + idArtista);
			return;
		}
		if (!artistaController.actualizarArtista(dto, idArtista)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idArtista + " actualizado");
	}

	private void eliminar(HttpExchange intercambio, Integer idArtista) throws IOException {
		if (artistaController.buscarArtista(idArtista) == null) {
			enviarError(intercambio, 404, "No existe el artista " + idArtista);
			return;
		}
		if (!artistaController.eliminarArtista(idArtista)) {
			enviarError(intercambio, 409, "No se puede eliminar: hay registros que lo usan");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idArtista + " eliminado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private ArtistaDTO desdeJson(String cuerpo, int idArtista) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String nombre = leerTexto(json, "nombre");
		if (nombre == null) {
			return null;
		}
		return new ArtistaDTO(idArtista, nombre);
	}

	private Map<String, Object> aMapa(ArtistaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idArtista", dto.idArtista());
		mapa.put("nombre", dto.nombre());
		return mapa;
	}
}
