package co.edu.unbosque.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.EjemplarController;
import co.edu.unbosque.dto.EjemplarDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/ejemplares
 *
 * GET  /api/ejemplares/disponibles?idCarta=1&numeroImpresion=2  catálogo para clientes, sin datos internos
 * GET  /api/ejemplares?estado=DISPONIBLE&idCarta=1              inventario completo (personal)
 * GET  /api/ejemplares/{id}                                     un ejemplar (personal)
 * POST /api/ejemplares                                          registra el ingreso (personal)
 * PUT  /api/ejemplares/{id}                                     cambia ubicación, condición y observaciones (personal)
 * PUT  /api/ejemplares/{id}/baja                                baja por daño o pérdida (personal)
 *
 * Cuerpo del ingreso: {"codigoEtiqueta": "MC-0011", "idCarta": 1, "numeroImpresion": 2,
 *   "codigoCondicion": "NM", "ubicacion": "Vitrina A-1", "observacionesDanos": null}
 */
public class EjemplarHandler extends ManejadorBase {

	private EjemplarController ejemplarController;

	public EjemplarHandler() {
		ejemplarController = new EjemplarController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("GET".equals(metodo) && "disponibles".equals(subruta)) {
			return CON_SESION;
		}
		return PERSONAL;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if ("disponibles".equals(subruta)) {
			if ("GET".equals(metodo)) {
				mostrarDisponibles(intercambio);
			} else {
				metodoNoPermitido(intercambio);
			}
			return;
		}
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

		String[] partes = subruta.split("/");
		Integer idEjemplar = convertirId(partes[0]);
		if (idEjemplar == null || partes.length > 2 || (partes.length == 2 && !"baja".equals(partes[1]))) {
			enviarError(intercambio, 404, "Ruta no encontrada");
			return;
		}
		if (partes.length == 2) {
			if ("PUT".equals(metodo)) {
				darDeBaja(intercambio, idEjemplar);
			} else {
				metodoNoPermitido(intercambio);
			}
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idEjemplar);
		} else if ("PUT".equals(metodo)) {
			actualizar(intercambio, idEjemplar);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrarDisponibles(HttpExchange intercambio) throws IOException {
		Integer idCarta = parametroEntero(intercambio, "idCarta");
		Integer numero = parametroEntero(intercambio, "numeroImpresion");
		if ((idCarta != null && idCarta < 0) || (numero != null && numero < 0)) {
			enviarError(intercambio, 400, "idCarta y numeroImpresion deben ser números enteros");
			return;
		}
		List<Object> lista = new ArrayList<>();
		for (EjemplarDTO dto : ejemplarController.mostrarEjemplares(EjemplarController.DISPONIBLE, idCarta, numero)) {
			lista.add(aMapaPublico(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		String estado = obtenerParametro(intercambio, "estado");
		if (estado != null) {
			estado = estado.trim().toUpperCase();
			if (!ejemplarController.esEstadoValido(estado)) {
				enviarError(intercambio, 400, "estado debe ser DISPONIBLE, RESERVADO, VENDIDO o BAJA");
				return;
			}
		}
		Integer idCarta = parametroEntero(intercambio, "idCarta");
		Integer numero = parametroEntero(intercambio, "numeroImpresion");
		if ((idCarta != null && idCarta < 0) || (numero != null && numero < 0)) {
			enviarError(intercambio, 400, "idCarta y numeroImpresion deben ser números enteros");
			return;
		}
		List<Object> lista = new ArrayList<>();
		for (EjemplarDTO dto : ejemplarController.mostrarEjemplares(estado, idCarta, numero)) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, int idEjemplar) throws IOException {
		EjemplarDTO dto = ejemplarController.buscarEjemplar(idEjemplar);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el ejemplar " + idEjemplar);
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
		Integer idCarta = leerEntero(json, "idCarta");
		Integer numero = leerEntero(json, "numeroImpresion");
		if (idCarta == null || numero == null) {
			enviarError(intercambio, 400, "idCarta y numeroImpresion son obligatorios");
			return;
		}
		LocalDate fecha = null;
		String fechaTexto = leerTexto(json, "fechaIngreso");
		if (fechaTexto != null && !fechaTexto.isBlank()) {
			try {
				fecha = LocalDate.parse(fechaTexto.trim());
			} catch (DateTimeParseException e) {
				enviarError(intercambio, 400, "fechaIngreso debe venir como AAAA-MM-DD");
				return;
			}
		}
		EjemplarDTO dto = new EjemplarDTO(0, leerTexto(json, "codigoEtiqueta"), leerTexto(json, "ubicacion"), null, null,
				fecha, leerTexto(json, "observacionesDanos"), idCarta, numero, leerTexto(json, "codigoCondicion"), null);
		String motivo = ejemplarController.validarIngreso(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		EjemplarDTO creado = ejemplarController.registrarIngreso(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo registrar el ejemplar");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, int idEjemplar) throws IOException {
		EjemplarDTO actual = ejemplarController.buscarEjemplar(idEjemplar);
		if (actual == null) {
			enviarError(intercambio, 404, "No existe el ejemplar " + idEjemplar);
			return;
		}
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		EjemplarDTO dto = new EjemplarDTO(idEjemplar, actual.codigoEtiqueta(), leerTexto(json, "ubicacion"),
				actual.estado(), actual.precioSugerido(), actual.fechaIngreso(), leerTexto(json, "observacionesDanos"),
				actual.idCarta(), actual.numeroImpresion(), leerTexto(json, "codigoCondicion"),
				actual.consecutivoSalida());
		String motivo = ejemplarController.validarActualizacion(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		if (!ejemplarController.actualizarEjemplar(dto, idEjemplar)) {
			enviarError(intercambio, 400, "No se pudo actualizar el ejemplar");
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(ejemplarController.buscarEjemplar(idEjemplar))));
	}

	private void darDeBaja(HttpExchange intercambio, int idEjemplar) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		String observaciones = json == null ? null : leerTexto(json, "observacionesDanos");
		String motivo = ejemplarController.darDeBajaEjemplar(idEjemplar, observaciones);
		if (motivo != null) {
			enviarError(intercambio, motivo.startsWith("No existe") ? 404 : 400, motivo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(ejemplarController.buscarEjemplar(idEjemplar))));
	}

	private Map<String, Object> aMapa(EjemplarDTO dto) {
		Map<String, Object> mapa = aMapaPublico(dto);
		mapa.put("ubicacion", dto.ubicacion());
		mapa.put("estado", dto.estado());
		mapa.put("fechaIngreso", dto.fechaIngreso() == null ? null : dto.fechaIngreso().toString());
		mapa.put("observacionesDanos", dto.observacionesDanos());
		mapa.put("consecutivoSalida", dto.consecutivoSalida());
		return mapa;
	}

	/** Lo único que ve un cliente: nada de ubicación ni observaciones internas (sección G). */
	private Map<String, Object> aMapaPublico(EjemplarDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idEjemplar", dto.idEjemplar());
		mapa.put("codigoEtiqueta", dto.codigoEtiqueta());
		mapa.put("idCarta", dto.idCarta());
		mapa.put("numeroImpresion", dto.numeroImpresion());
		mapa.put("codigoCondicion", dto.codigoCondicion());
		mapa.put("precioSugerido", dto.precioSugerido());
		return mapa;
	}
}
