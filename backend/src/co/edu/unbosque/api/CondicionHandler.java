package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.CondicionController;
import co.edu.unbosque.dto.CondicionDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/condiciones
 *
 * GET  /api/condiciones         lista todas
 * GET  /api/condiciones/{codigo} busca una
 * POST /api/condiciones         crea  (cuerpo: codigoCondicion, nombre, factorAjuste)
 * PUT  /api/condiciones/{codigo} actualiza nombre y factorAjuste
 * DELETE /api/condiciones/{codigo} elimina (solo si ningún ejemplar la usa)
 */
public class CondicionHandler extends ManejadorBase {

	private CondicionController condicionController;

	public CondicionHandler() {
		condicionController = new CondicionController();
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String codigo = obtenerSubruta(intercambio);

		if ("GET".equals(metodo) && codigo.isEmpty()) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, codigo);
		} else if ("POST".equals(metodo) && codigo.isEmpty()) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && !codigo.isEmpty()) {
			actualizar(intercambio, codigo);
		} else if ("DELETE".equals(metodo) && !codigo.isEmpty()) {
			eliminar(intercambio, codigo);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (CondicionDTO dto : condicionController.mostrarCondiciones()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, String codigo) throws IOException {
		CondicionDTO dto = condicionController.buscarCondicion(codigo);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la condición " + codigo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		CondicionDTO dto = desdeJson(leerCuerpo(intercambio), null);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos: se esperan codigoCondicion, nombre y factorAjuste");
			return;
		}
		if (!condicionController.agregarCondicion(dto)) {
			enviarError(intercambio, 400, "No se pudo crear: el código ya existe o el factor no está entre 0 y 1");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(dto)));
	}

	private void actualizar(HttpExchange intercambio, String codigo) throws IOException {
		CondicionDTO dto = desdeJson(leerCuerpo(intercambio), codigo);
		if (dto == null) {
			enviarError(intercambio, 400, "Datos incompletos: se esperan nombre y factorAjuste");
			return;
		}
		if (condicionController.buscarCondicion(codigo) == null) {
			enviarError(intercambio, 404, "No existe la condición " + codigo);
			return;
		}
		if (!condicionController.actualizarCondicion(dto, codigo)) {
			enviarError(intercambio, 400, "No se pudo actualizar: revise que el factor esté entre 0 y 1");
			return;
		}
		enviarMensaje(intercambio, 200, "Condición " + codigo + " actualizada");
	}

	private void eliminar(HttpExchange intercambio, String codigo) throws IOException {
		if (condicionController.buscarCondicion(codigo) == null) {
			enviarError(intercambio, 404, "No existe la condición " + codigo);
			return;
		}
		if (!condicionController.eliminarCondicion(codigo)) {
			enviarError(intercambio, 409, "No se puede eliminar: hay ejemplares con esta condición");
			return;
		}
		enviarMensaje(intercambio, 200, "Condición " + codigo + " eliminada");
	}

	private Map<String, Object> aMapa(CondicionDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("codigoCondicion", dto.codigoCondicion());
		mapa.put("nombre", dto.nombre());
		mapa.put("factorAjuste", dto.factorAjuste());
		return mapa;
	}

	/** Convierte el cuerpo en DTO; si se da el código de la ruta, ese manda. */
	private CondicionDTO desdeJson(String cuerpo, String codigoRuta) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		String codigo = codigoRuta != null ? codigoRuta : leerTexto(json, "codigoCondicion");
		String nombre = leerTexto(json, "nombre");
		Double factor = leerNumero(json, "factorAjuste");
		if (codigo == null || nombre == null || factor == null) {
			return null;
		}
		return new CondicionDTO(codigo, nombre, factor);
	}
}
