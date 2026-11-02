package co.edu.unbosque.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.EdicionController;
import co.edu.unbosque.dto.EdicionDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/ediciones
 *
 * GET    /api/ediciones          lista
 * GET    /api/ediciones/{id}     busca uno
 * POST   /api/ediciones          crea
 * PUT    /api/ediciones/{id}     actualiza
 * DELETE /api/ediciones/{id}     elimina (solo si nadie lo usa)
 */
public class EdicionHandler extends ManejadorBase {

	private EdicionController edicionController;

	public EdicionHandler() {
		edicionController = new EdicionController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idEdicion = null;
		if (!subruta.isEmpty()) {
			idEdicion = convertirId(subruta);
			if (idEdicion == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idEdicion == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idEdicion);
		} else if ("POST".equals(metodo) && idEdicion == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idEdicion != null) {
			actualizar(intercambio, idEdicion);
		} else if ("DELETE".equals(metodo) && idEdicion != null) {
			eliminar(intercambio, idEdicion);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (EdicionDTO dto : edicionController.mostrarEdiciones()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idEdicion) throws IOException {
		EdicionDTO dto = edicionController.buscarEdicion(idEdicion);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la edición " + idEdicion);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		EdicionDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados: se espera codigoImpreso, nombre, fechaLanzamiento, cantidadCartas, tipoEdicion");
			return;
		}
		EdicionDTO creado = edicionController.agregarEdicion(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo crear: datos inválidos o código repetido");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, Integer idEdicion) throws IOException {
		EdicionDTO dto = desdeJson(leerCuerpo(intercambio), idEdicion);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos o mal formados");
			return;
		}
		if (edicionController.buscarEdicion(idEdicion) == null) {
			enviarError(intercambio, 404, "No existe la edición " + idEdicion);
			return;
		}
		if (!edicionController.actualizarEdicion(dto, idEdicion)) {
			enviarError(intercambio, 400, "No se pudo actualizar: datos inválidos o repetidos");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idEdicion + " actualizado");
	}

	private void eliminar(HttpExchange intercambio, Integer idEdicion) throws IOException {
		if (edicionController.buscarEdicion(idEdicion) == null) {
			enviarError(intercambio, 404, "No existe la edición " + idEdicion);
			return;
		}
		if (!edicionController.eliminarEdicion(idEdicion)) {
			enviarError(intercambio, 409, "No se puede eliminar: hay registros que lo usan");
			return;
		}
		enviarMensaje(intercambio, 200, "Registro " + idEdicion + " eliminado");
	}

	/** Convierte el cuerpo en DTO; el id viene de la ruta (0 al crear). */
	private EdicionDTO desdeJson(String cuerpo, int idEdicion) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String codigoImpreso = leerTexto(json, "codigoImpreso");
		String nombre = leerTexto(json, "nombre");
		LocalDate fechaLanzamiento = null;
		String fechaLanzamientoTexto = leerTexto(json, "fechaLanzamiento");
		if (fechaLanzamientoTexto != null && !fechaLanzamientoTexto.isBlank()) {
			try {
				fechaLanzamiento = LocalDate.parse(fechaLanzamientoTexto);
			} catch (DateTimeParseException e) {
				return null; // la fecha debe venir como AAAA-MM-DD
			}
		}
		Integer cantidadCartas = leerEntero(json, "cantidadCartas");
		if (json.get("cantidadCartas") != null && cantidadCartas == null) {
			return null; // vino pero no es entero
		}
		String tipoEdicion = leerTexto(json, "tipoEdicion");
		if (codigoImpreso == null || nombre == null) {
			return null;
		}
		return new EdicionDTO(idEdicion, codigoImpreso, nombre, fechaLanzamiento, cantidadCartas, tipoEdicion);
	}

	private Map<String, Object> aMapa(EdicionDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idEdicion", dto.idEdicion());
		mapa.put("codigoImpreso", dto.codigoImpreso());
		mapa.put("nombre", dto.nombre());
		mapa.put("fechaLanzamiento", dto.fechaLanzamiento() == null ? null : dto.fechaLanzamiento().toString());
		mapa.put("cantidadCartas", dto.cantidadCartas());
		mapa.put("tipoEdicion", dto.tipoEdicion());
		return mapa;
	}
}
