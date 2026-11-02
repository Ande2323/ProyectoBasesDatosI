package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.CartaController;
import co.edu.unbosque.dto.CartaDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/cartas
 *
 * GET    /api/cartas               lista todas
 * GET    /api/cartas?nombre=bolt   busca por parte del nombre
 * GET    /api/cartas/{id}          busca una
 * POST   /api/cartas               crea (con sus colores y tipos)
 * PUT    /api/cartas/{id}          actualiza (reemplaza colores y tipos)
 * DELETE /api/cartas/{id}          elimina (solo si no tiene impresiones)
 *
 * Cuerpo: {"nombreOficial": "...", "textoOraculo": "...", "costeMana": "{1}{R}",
 *          "poder": "2", "resistencia": "2", "lealtad": null,
 *          "colores": [4], "tipos": [2, 5]}
 * valorMana viene en las respuestas, pero no se envía: se calcula.
 */
public class CartaHandler extends ManejadorBase {

	private CartaController cartaController;

	public CartaHandler() {
		cartaController = new CartaController();
	}

	/** Cartas e impresiones las registra el personal; borrarlas es solo del administrador. */
	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("GET".equals(metodo)) {
			return CON_SESION;
		}
		if ("DELETE".equals(metodo)) {
			return SOLO_ADMIN;
		}
		return PERSONAL;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		Integer idCarta = null;
		if (!subruta.isEmpty()) {
			idCarta = convertirId(subruta);
			if (idCarta == null) {
				enviarError(intercambio, 400, "El id debe ser un número entero");
				return;
			}
		}

		if ("GET".equals(metodo) && idCarta == null) {
			mostrar(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idCarta);
		} else if ("POST".equals(metodo) && idCarta == null) {
			crear(intercambio);
		} else if ("PUT".equals(metodo) && idCarta != null) {
			actualizar(intercambio, idCarta);
		} else if ("DELETE".equals(metodo) && idCarta != null) {
			eliminar(intercambio, idCarta);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		String nombre = obtenerParametro(intercambio, "nombre");
		List<Object> lista = new ArrayList<>();
		for (CartaDTO dto : cartaController.buscarCartasPorNombre(nombre)) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, Integer idCarta) throws IOException {
		CartaDTO dto = cartaController.buscarCarta(idCarta);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la carta " + idCarta);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		CartaDTO dto = desdeJson(leerCuerpo(intercambio), 0);
		if (dto == null) {
			enviarError(intercambio, 400, "JSON mal formado: colores y tipos deben ser listas de números (sus id)");
			return;
		}
		String motivo = cartaController.validarCarta(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		CartaDTO creada = cartaController.agregarCarta(dto);
		if (creada == null) {
			enviarError(intercambio, 400, "No se pudo crear: el nombre ya existe, o algún color o tipo no existe");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creada)));
	}

	private void actualizar(HttpExchange intercambio, Integer idCarta) throws IOException {
		CartaDTO dto = desdeJson(leerCuerpo(intercambio), idCarta);
		if (dto == null) {
			enviarError(intercambio, 400, "JSON mal formado: colores y tipos deben ser listas de números (sus id)");
			return;
		}
		String motivo = cartaController.validarCarta(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		if (cartaController.buscarCarta(idCarta) == null) {
			enviarError(intercambio, 404, "No existe la carta " + idCarta);
			return;
		}
		if (!cartaController.actualizarCarta(dto, idCarta)) {
			enviarError(intercambio, 400, "No se pudo actualizar: el nombre ya existe, o algún color o tipo no existe");
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(cartaController.buscarCarta(idCarta))));
	}

	private void eliminar(HttpExchange intercambio, Integer idCarta) throws IOException {
		if (cartaController.buscarCarta(idCarta) == null) {
			enviarError(intercambio, 404, "No existe la carta " + idCarta);
			return;
		}
		if (!cartaController.eliminarCarta(idCarta)) {
			enviarError(intercambio, 409, "No se puede eliminar: la carta ya tiene impresiones registradas");
			return;
		}
		enviarMensaje(intercambio, 200, "Carta " + idCarta + " eliminada");
	}

	/** Retorna null solo si el JSON está mal formado; las reglas de negocio las revisa el controller. */
	private CartaDTO desdeJson(String cuerpo, int idCarta) {
		Map<String, Object> json = JsonUtil.leerObjeto(cuerpo);
		if (json == null) {
			return null;
		}
		List<Integer> colores = leerListaEnteros(json, "colores");
		List<Integer> tipos = leerListaEnteros(json, "tipos");
		if ((colores == null && json.get("colores") != null) || (tipos == null && json.get("tipos") != null)) {
			return null;
		}
		return new CartaDTO(idCarta, leerTexto(json, "nombreOficial"), leerTexto(json, "textoOraculo"),
				leerTexto(json, "costeMana"), leerTexto(json, "poder"), leerTexto(json, "resistencia"),
				leerTexto(json, "lealtad"), colores, tipos, 0);
	}

	private Map<String, Object> aMapa(CartaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idCarta", dto.idCarta());
		mapa.put("nombreOficial", dto.nombreOficial());
		mapa.put("textoOraculo", dto.textoOraculo());
		mapa.put("costeMana", dto.costeMana());
		mapa.put("valorMana", dto.valorMana());
		mapa.put("poder", dto.poder());
		mapa.put("resistencia", dto.resistencia());
		mapa.put("lealtad", dto.lealtad());
		mapa.put("colores", new ArrayList<Object>(dto.colores()));
		mapa.put("tipos", new ArrayList<Object>(dto.tipos()));
		return mapa;
	}
}
