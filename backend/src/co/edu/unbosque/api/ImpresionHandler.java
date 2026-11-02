package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.ImpresionController;
import co.edu.unbosque.dto.ImpresionDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/impresiones. La llave es doble: /{idCarta}/{numeroImpresion}.
 *
 * GET    /api/impresiones                        lista todas
 * GET    /api/impresiones?idCarta=1              impresiones de una carta
 * GET    /api/impresiones/1/2                    busca una
 * GET    /api/impresiones/1/1/reimpresiones      reimpresiones de una original
 * POST   /api/impresiones                        crea (el número lo asigna el backend)
 * PUT    /api/impresiones/1/2                    actualiza
 * DELETE /api/impresiones/1/2                    elimina (si no tiene ejemplares, precios ni reimpresiones)
 *
 * Cuerpo: {"idCarta": 1, "numeroColeccion": "146", "foil": false, "idEdicion": 2,
 *          "idIdioma": 1, "idRareza": 1, "idArtista": 2,
 *          "originalNumeroImpresion": 1}
 * Para una primera edición, originalNumeroImpresion va en null.
 */
public class ImpresionHandler extends ManejadorBase {

	private ImpresionController impresionController;

	public ImpresionHandler() {
		impresionController = new ImpresionController();
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
		String[] partes = subruta.isEmpty() ? new String[0] : subruta.split("/");

		if (partes.length == 0) {
			if ("GET".equals(metodo)) {
				mostrar(intercambio);
			} else if ("POST".equals(metodo)) {
				crear(intercambio);
			} else {
				metodoNoPermitido(intercambio);
			}
			return;
		}

		boolean conReimpresiones = partes.length == 3 && "reimpresiones".equals(partes[2]);
		if (partes.length != 2 && !conReimpresiones) {
			enviarError(intercambio, 404, "Ruta no encontrada: use /api/impresiones/{idCarta}/{numeroImpresion}");
			return;
		}
		Integer idCarta = convertirId(partes[0]);
		Integer numero = convertirId(partes[1]);
		if (idCarta == null || numero == null) {
			enviarError(intercambio, 400, "idCarta y numeroImpresion deben ser números enteros");
			return;
		}

		if (conReimpresiones && "GET".equals(metodo)) {
			mostrarReimpresiones(intercambio, idCarta, numero);
		} else if (conReimpresiones) {
			metodoNoPermitido(intercambio);
		} else if ("GET".equals(metodo)) {
			buscar(intercambio, idCarta, numero);
		} else if ("PUT".equals(metodo)) {
			actualizar(intercambio, idCarta, numero);
		} else if ("DELETE".equals(metodo)) {
			eliminar(intercambio, idCarta, numero);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		String parametro = obtenerParametro(intercambio, "idCarta");
		List<ImpresionDTO> impresiones;
		if (parametro == null) {
			impresiones = impresionController.mostrarImpresiones();
		} else {
			Integer idCarta = convertirId(parametro);
			if (idCarta == null) {
				enviarError(intercambio, 400, "idCarta debe ser un número entero");
				return;
			}
			impresiones = impresionController.mostrarImpresionesDeCarta(idCarta);
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aLista(impresiones)));
	}

	private void mostrarReimpresiones(HttpExchange intercambio, int idCarta, int numero) throws IOException {
		if (impresionController.buscarImpresion(idCarta, numero) == null) {
			enviarError(intercambio, 404, "No existe la impresión " + idCarta + "/" + numero);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aLista(impresionController.mostrarReimpresiones(idCarta, numero))));
	}

	private void buscar(HttpExchange intercambio, int idCarta, int numero) throws IOException {
		ImpresionDTO dto = impresionController.buscarImpresion(idCarta, numero);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la impresión " + idCarta + "/" + numero);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		Integer idCarta = json == null ? null : leerEntero(json, "idCarta");
		if (idCarta == null) {
			enviarError(intercambio, 400, "Falta idCarta o el JSON está mal formado");
			return;
		}
		ImpresionDTO dto = desdeJson(json, idCarta, 0);
		if (dto == null) {
			enviarError(intercambio, 400, "Tipos de dato inválidos: los id son números, foil es true/false");
			return;
		}
		String motivo = impresionController.validarImpresion(dto, null);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		ImpresionDTO creada = impresionController.agregarImpresion(dto);
		if (creada == null) {
			enviarError(intercambio, 400,
					"No se pudo crear: ya existe esa edición + idioma + número + acabado, o algún catálogo no existe");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creada)));
	}

	private void actualizar(HttpExchange intercambio, int idCarta, int numero) throws IOException {
		if (impresionController.buscarImpresion(idCarta, numero) == null) {
			enviarError(intercambio, 404, "No existe la impresión " + idCarta + "/" + numero);
			return;
		}
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		ImpresionDTO dto = json == null ? null : desdeJson(json, idCarta, numero);
		if (dto == null) {
			enviarError(intercambio, 400, "JSON mal formado o tipos de dato inválidos");
			return;
		}
		String motivo = impresionController.validarImpresion(dto, numero);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		if (!impresionController.actualizarImpresion(dto, idCarta, numero)) {
			enviarError(intercambio, 400,
					"No se pudo actualizar: ya existe esa edición + idioma + número + acabado, o algún catálogo no existe");
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(impresionController.buscarImpresion(idCarta, numero))));
	}

	private void eliminar(HttpExchange intercambio, int idCarta, int numero) throws IOException {
		if (impresionController.buscarImpresion(idCarta, numero) == null) {
			enviarError(intercambio, 404, "No existe la impresión " + idCarta + "/" + numero);
			return;
		}
		if (!impresionController.eliminarImpresion(idCarta, numero)) {
			enviarError(intercambio, 409, "No se puede eliminar: tiene ejemplares, precios o reimpresiones registradas");
			return;
		}
		enviarMensaje(intercambio, 200, "Impresión " + idCarta + "/" + numero + " eliminada");
	}

	/** Retorna null si algún dato viene con el tipo equivocado; los faltantes quedan en 0 y los valida el controller. */
	private ImpresionDTO desdeJson(Map<String, Object> json, int idCarta, int numero) {
		Integer idEdicion = leerEntero(json, "idEdicion");
		Integer idIdioma = leerEntero(json, "idIdioma");
		Integer idRareza = leerEntero(json, "idRareza");
		Integer idArtista = leerEntero(json, "idArtista");
		Integer original = leerEntero(json, "originalNumeroImpresion");
		Boolean foil = leerBooleano(json, "foil");
		for (String clave : new String[] { "idEdicion", "idIdioma", "idRareza", "idArtista", "originalNumeroImpresion" }) {
			if (json.get(clave) != null && leerEntero(json, clave) == null) {
				return null;
			}
		}
		if (json.get("foil") != null && foil == null) {
			return null;
		}
		return new ImpresionDTO(idCarta, numero, leerTexto(json, "numeroColeccion"), foil != null && foil,
				valor(idEdicion), valor(idIdioma), valor(idRareza), valor(idArtista), original);
	}

	private int valor(Integer numero) {
		return numero == null ? 0 : numero;
	}

	private List<Object> aLista(List<ImpresionDTO> impresiones) {
		List<Object> lista = new ArrayList<>();
		for (ImpresionDTO dto : impresiones) {
			lista.add(aMapa(dto));
		}
		return lista;
	}

	private Map<String, Object> aMapa(ImpresionDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idCarta", dto.idCarta());
		mapa.put("numeroImpresion", dto.numeroImpresion());
		mapa.put("numeroColeccion", dto.numeroColeccion());
		mapa.put("foil", dto.foil());
		mapa.put("idEdicion", dto.idEdicion());
		mapa.put("idIdioma", dto.idIdioma());
		mapa.put("idRareza", dto.idRareza());
		mapa.put("idArtista", dto.idArtista());
		mapa.put("originalNumeroImpresion", dto.originalNumeroImpresion());
		mapa.put("primeraEdicion", dto.esPrimeraEdicion());
		return mapa;
	}
}
