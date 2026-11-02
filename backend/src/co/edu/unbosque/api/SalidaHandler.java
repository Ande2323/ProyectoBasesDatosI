package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.SalidaController;
import co.edu.unbosque.dto.SalidaDTO;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/salidas (personal). Quien registra es el empleado de la sesión.
 *
 * GET  /api/salidas       lista
 * GET  /api/salidas/{n}   una salida
 * POST /api/salidas       registra una salida, de dos formas:
 *
 * Entrega directa: {"clienteTipoDocumento": "CC", "clienteNumeroDocumento": "80123456",
 *                   "ejemplares": [1, 6], "observaciones": "..."}
 * Reclamo de reserva: {"consecutivoReserva": 1, "ejemplares": [3], "observaciones": "..."}
 *   Sin "ejemplares" se entregan todos los de la reserva.
 */
public class SalidaHandler extends ManejadorBase {

	private SalidaController salidaController;

	public SalidaHandler() {
		salidaController = new SalidaController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return PERSONAL;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if (subruta.isEmpty()) {
			if ("GET".equals(metodo)) {
				mostrar(intercambio);
			} else if ("POST".equals(metodo)) {
				registrar(intercambio);
			} else {
				metodoNoPermitido(intercambio);
			}
			return;
		}
		Integer consecutivo = convertirId(subruta);
		if (consecutivo == null) {
			enviarError(intercambio, 404, "Ruta no encontrada");
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, consecutivo);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (SalidaDTO dto : salidaController.mostrarSalidas()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, int consecutivo) throws IOException {
		SalidaDTO dto = salidaController.buscarSalida(consecutivo);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la salida " + consecutivo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void registrar(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		List<Integer> ejemplares = leerListaEnteros(json, "ejemplares");
		if ((json.get("ejemplares") != null && ejemplares == null) || enteroInvalido(json, "consecutivoReserva")) {
			enviarError(intercambio, 400, "ejemplares debe ser una lista de id y consecutivoReserva un número");
			return;
		}
		Integer reserva = leerEntero(json, "consecutivoReserva");
		String tipo = leerTexto(json, "clienteTipoDocumento");
		String numero = leerTexto(json, "clienteNumeroDocumento");
		SalidaDTO dto = new SalidaDTO(0, null, leerTexto(json, "observaciones"), null,
				tipo == null ? null : tipo.trim().toUpperCase(), numero == null ? null : numero.trim().toUpperCase(),
				null, null, ejemplares, reserva);

		String motivo = reserva == null ? salidaController.validarEntregaDirecta(dto)
				: salidaController.validarReclamo(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		SesionActiva empleado = obtenerSesion(intercambio);
		SalidaDTO creada = reserva == null ? salidaController.registrarEntregaDirecta(dto, empleado)
				: salidaController.registrarReclamo(dto, empleado);
		if (creada == null) {
			enviarError(intercambio, 409, "No se pudo registrar: algún ejemplar o la reserva acaban de cambiar de estado");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creada)));
	}

	private Map<String, Object> aMapa(SalidaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("consecutivoSalida", dto.consecutivoSalida());
		mapa.put("motivo", dto.motivo());
		mapa.put("observaciones", dto.observaciones());
		mapa.put("fecha", dto.fecha() == null ? null : dto.fecha().toString());
		mapa.put("clienteTipoDocumento", dto.clienteTipoDocumento());
		mapa.put("clienteNumeroDocumento", dto.clienteNumeroDocumento());
		mapa.put("empleadoTipoDocumento", dto.empleadoTipoDocumento());
		mapa.put("empleadoNumeroDocumento", dto.empleadoNumeroDocumento());
		mapa.put("consecutivoReserva", dto.consecutivoReserva());
		mapa.put("ejemplares", new ArrayList<Object>(dto.ejemplares()));
		return mapa;
	}
}
