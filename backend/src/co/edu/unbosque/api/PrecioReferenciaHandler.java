package co.edu.unbosque.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.PrecioReferenciaController;
import co.edu.unbosque.dto.PrecioReferenciaDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/precios. Solo se consultan y se agregan: el historial no cambia.
 *
 * GET  /api/precios?idCarta=1&numeroImpresion=2           historial, del más reciente al más antiguo
 * GET  /api/precios/vigente?idCarta=1&numeroImpresion=2   precio vigente
 * POST /api/precios                                       carga un precio (personal)
 *
 * Cuerpo: {"idCarta": 1, "numeroImpresion": 2, "valor": 12000, "fecha": "2026-10-01",
 *          "fuenteCotizacion": "TCGplayer"}. Si no se envía la fecha, se toma la de hoy.
 */
public class PrecioReferenciaHandler extends ManejadorBase {

	private PrecioReferenciaController precioReferenciaController;

	public PrecioReferenciaHandler() {
		precioReferenciaController = new PrecioReferenciaController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return "GET".equals(metodo) ? CON_SESION : PERSONAL;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if ("GET".equals(metodo) && (subruta.isEmpty() || "vigente".equals(subruta))) {
			consultar(intercambio, "vigente".equals(subruta));
		} else if ("POST".equals(metodo) && subruta.isEmpty()) {
			registrar(intercambio);
		} else if (!subruta.isEmpty() && !"vigente".equals(subruta)) {
			enviarError(intercambio, 404, "Ruta no encontrada");
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void consultar(HttpExchange intercambio, boolean soloVigente) throws IOException {
		Integer idCarta = parametroEntero(intercambio, "idCarta");
		Integer numero = parametroEntero(intercambio, "numeroImpresion");
		if (idCarta == null || numero == null || idCarta < 0 || numero < 0) {
			enviarError(intercambio, 400, "Indique idCarta y numeroImpresion como números enteros");
			return;
		}
		if (soloVigente) {
			PrecioReferenciaDTO dto = precioReferenciaController.buscarPrecioVigente(idCarta, numero);
			if (dto == null) {
				enviarError(intercambio, 404, "La impresión " + idCarta + "/" + numero + " no tiene precios");
				return;
			}
			enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
			return;
		}
		List<Object> lista = new ArrayList<>();
		for (PrecioReferenciaDTO dto : precioReferenciaController.mostrarHistorial(idCarta, numero)) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void registrar(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		Integer idCarta = leerEntero(json, "idCarta");
		Integer numero = leerEntero(json, "numeroImpresion");
		if (idCarta == null || numero == null) {
			enviarError(intercambio, 400, "idCarta y numeroImpresion son obligatorios");
			return;
		}
		LocalDate fecha = null;
		String fechaTexto = leerTexto(json, "fecha");
		if (fechaTexto != null && !fechaTexto.isBlank()) {
			try {
				fecha = LocalDate.parse(fechaTexto.trim());
			} catch (DateTimeParseException e) {
				enviarError(intercambio, 400, "La fecha debe venir como AAAA-MM-DD");
				return;
			}
		}
		PrecioReferenciaDTO dto = new PrecioReferenciaDTO(idCarta, numero, fecha, leerDecimal(json, "valor"),
				leerTexto(json, "fuenteCotizacion"));
		String motivo = precioReferenciaController.validarPrecio(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		PrecioReferenciaDTO creado = precioReferenciaController.registrarPrecio(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo registrar el precio");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private Map<String, Object> aMapa(PrecioReferenciaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idCarta", dto.idCarta());
		mapa.put("numeroImpresion", dto.numeroImpresion());
		mapa.put("fecha", dto.fecha().toString());
		mapa.put("valor", dto.valor());
		mapa.put("fuenteCotizacion", dto.fuenteCotizacion());
		return mapa;
	}
}
