package co.edu.unbosque.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import co.edu.unbosque.util.Configuracion;
import co.edu.unbosque.util.GestorSesiones;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.JsonUtil;

/**
 * Base de todos los manejadores HTTP: agrega las cabeceras CORS para Angular,
 * responde el preflight OPTIONS, revisa la sesión y el rol, captura errores y
 * ofrece utilidades para leer el cuerpo y responder JSON.
 */
public abstract class ManejadorBase implements HttpHandler {

	/** Cualquiera, sin iniciar sesión (por ejemplo el login o el registro de clientes). */
	protected static final List<String> PUBLICO = null;
	/** Cualquier usuario con sesión, sin importar el rol. */
	protected static final List<String> CON_SESION = List.of();
	protected static final List<String> PERSONAL = List.of("EMP", "ADM");
	protected static final List<String> SOLO_ADMIN = List.of("ADM");
	protected static final List<String> SOLO_CLIENTE = List.of("CLI");

	@Override
	public void handle(HttpExchange intercambio) throws IOException {
		try {
			agregarCabecerasCors(intercambio);
			String metodo = intercambio.getRequestMethod();
			if ("OPTIONS".equals(metodo)) {
				intercambio.sendResponseHeaders(204, -1);
				return;
			}
			if (!tienePermiso(intercambio, rolesPermitidos(metodo, obtenerSubruta(intercambio)))) {
				return;
			}
			atender(intercambio);
		} catch (Exception e) {
			e.printStackTrace();
			enviarError(intercambio, 500, "Error interno del servidor");
		} finally {
			intercambio.close();
		}
	}

	protected abstract void atender(HttpExchange intercambio) throws IOException;

	/**
	 * Quién puede usar cada método de este recurso (sección G del documento de
	 * supuestos). Por defecto consultar requiere sesión y modificar es solo del
	 * administrador, que es lo que corresponde a los catálogos. Cada handler lo
	 * sobrescribe si sus reglas son otras.
	 */
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("GET".equals(metodo)) {
			return CON_SESION;
		}
		return SOLO_ADMIN;
	}

	/** Responde 401 o 403 y retorna false si quien llama no puede usar la ruta. */
	private boolean tienePermiso(HttpExchange intercambio, List<String> roles) {
		if (roles == PUBLICO) {
			return true;
		}
		SesionActiva sesion = obtenerSesion(intercambio);
		if (sesion == null) {
			enviarError(intercambio, 401, "Debe iniciar sesión");
			return false;
		}
		if (!roles.isEmpty() && !roles.contains(sesion.codigoRol())) {
			enviarError(intercambio, 403, "Su rol no tiene permiso para esta acción");
			return false;
		}
		return true;
	}

	/** La sesión de quien llama según la cabecera Authorization, o null si no hay. */
	protected SesionActiva obtenerSesion(HttpExchange intercambio) {
		return GestorSesiones.buscarSesion(obtenerToken(intercambio));
	}

	protected String obtenerToken(HttpExchange intercambio) {
		String cabecera = intercambio.getRequestHeaders().getFirst("Authorization");
		if (cabecera == null || !cabecera.startsWith("Bearer ")) {
			return null;
		}
		return cabecera.substring("Bearer ".length()).trim();
	}

	/** cors.origen admite varios orígenes separados por coma: desarrollo y AWS. */
	private void agregarCabecerasCors(HttpExchange intercambio) {
		Headers cabeceras = intercambio.getResponseHeaders();
		String origen = intercambio.getRequestHeaders().getFirst("Origin");
		for (String permitido : Configuracion.obtener("cors.origen", "http://localhost:4200").split(",")) {
			if (permitido.trim().equals(origen)) {
				cabeceras.set("Access-Control-Allow-Origin", origen);
				cabeceras.set("Vary", "Origin");
			}
		}
		cabeceras.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		cabeceras.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
	}

	/**
	 * Lo que viene después de la ruta del contexto. Para el contexto
	 * /api/condiciones y la petición /api/condiciones/NM retorna "NM"; si no hay
	 * nada más retorna "".
	 */
	protected String obtenerSubruta(HttpExchange intercambio) {
		String ruta = intercambio.getRequestURI().getPath();
		String contexto = intercambio.getHttpContext().getPath();
		String resto = ruta.substring(contexto.length());
		while (resto.startsWith("/")) {
			resto = resto.substring(1);
		}
		while (resto.endsWith("/")) {
			resto = resto.substring(0, resto.length() - 1);
		}
		return resto;
	}

	protected String leerCuerpo(HttpExchange intercambio) throws IOException {
		try (InputStream entrada = intercambio.getRequestBody()) {
			return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	protected void enviarJson(HttpExchange intercambio, int codigo, String json) throws IOException {
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		intercambio.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
		intercambio.sendResponseHeaders(codigo, bytes.length);
		try (OutputStream salida = intercambio.getResponseBody()) {
			salida.write(bytes);
		}
	}

	protected void enviarMensaje(HttpExchange intercambio, int codigo, String mensaje) throws IOException {
		Map<String, Object> cuerpo = new LinkedHashMap<>();
		cuerpo.put("mensaje", mensaje);
		enviarJson(intercambio, codigo, JsonUtil.aJson(cuerpo));
	}

	protected void enviarError(HttpExchange intercambio, int codigo, String mensaje) {
		try {
			Map<String, Object> cuerpo = new LinkedHashMap<>();
			cuerpo.put("error", mensaje);
			enviarJson(intercambio, codigo, JsonUtil.aJson(cuerpo));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void metodoNoPermitido(HttpExchange intercambio) throws IOException {
		enviarError(intercambio, 405, "Método no permitido: " + intercambio.getRequestMethod());
	}

	/** Lee un número del JSON; retorna null si falta o no es numérico. */
	protected Double leerNumero(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof Number numero) {
			return numero.doubleValue();
		}
		return null;
	}

	/** Lee un entero del JSON; retorna null si falta, no es numérico o tiene decimales. */
	protected Integer leerEntero(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof Long numero && numero >= Integer.MIN_VALUE && numero <= Integer.MAX_VALUE) {
			return numero.intValue();
		}
		return null;
	}

	/** Convierte el id que viene en la ruta (/api/rarezas/3); retorna null si no es un entero. */
	protected Integer convertirId(String texto) {
		try {
			return Integer.valueOf(texto);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Lee un booleano del JSON; retorna null si falta o no es true/false. */
	protected Boolean leerBooleano(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof Boolean booleano) {
			return booleano;
		}
		return null;
	}

	/** Lee un decimal exacto del JSON (dinero); retorna null si falta o no es numérico. */
	protected BigDecimal leerDecimal(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof Number numero) {
			return new BigDecimal(numero.toString());
		}
		return null;
	}

	/** Convierte una lista JSON de enteros; retorna null si falta o algún elemento no es entero. */
	protected List<Integer> leerListaEnteros(Map<String, Object> json, String clave) {
		List<Object> lista = leerLista(json, clave);
		if (lista == null) {
			return null;
		}
		List<Integer> enteros = new ArrayList<>();
		for (Object valor : lista) {
			if (!(valor instanceof Long numero)) {
				return null;
			}
			enteros.add(numero.intValue());
		}
		return enteros;
	}

	/** Parámetro entero de la URL. Retorna null si no viene y -1 si viene mal escrito. */
	protected Integer parametroEntero(HttpExchange intercambio, String nombre) {
		String texto = obtenerParametro(intercambio, nombre);
		if (texto == null) {
			return null;
		}
		Integer valor = convertirId(texto);
		return valor == null ? Integer.valueOf(-1) : valor;
	}

	/** True si la clave vino en el JSON pero no es un entero. */
	protected boolean enteroInvalido(Map<String, Object> json, String clave) {
		return json.get(clave) != null && leerEntero(json, clave) == null;
	}

	/** Lee una lista del JSON; retorna null si falta o no es una lista. */
	protected List<Object> leerLista(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof List<?> lista) {
			return new ArrayList<>(lista);
		}
		return null;
	}

	/** Parámetro de la URL: para /api/cartas?nombre=bolt, obtenerParametro(x, "nombre") da "bolt". */
	protected String obtenerParametro(HttpExchange intercambio, String nombre) {
		String consulta = intercambio.getRequestURI().getRawQuery();
		if (consulta == null) {
			return null;
		}
		for (String par : consulta.split("&")) {
			String[] partes = par.split("=", 2);
			if (URLDecoder.decode(partes[0], StandardCharsets.UTF_8).equals(nombre)) {
				return partes.length > 1 ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
			}
		}
		return null;
	}

	/** Lee un texto del JSON; retorna null si falta o no es texto. */
	protected String leerTexto(Map<String, Object> json, String clave) {
		Object valor = json.get(clave);
		if (valor instanceof String texto) {
			return texto;
		}
		return null;
	}
}
