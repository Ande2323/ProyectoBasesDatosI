package co.edu.unbosque.api;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.SesionController;
import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/sesion
 *
 * POST   /api/sesion             inicia sesión: {"nombreUsuario": "...", "contrasena": "..."} -> token
 * GET    /api/sesion             datos de quien está conectado
 * DELETE /api/sesion             cierra la sesión
 * PUT    /api/sesion/contrasena  cambia la propia contraseña: {"actual": "...", "nueva": "..."}
 *
 * Con el token, Angular envía en cada petición: Authorization: Bearer <token>
 */
public class SesionHandler extends ManejadorBase {

	private SesionController sesionController;

	public SesionHandler() {
		sesionController = new SesionController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("POST".equals(metodo) && subruta.isEmpty()) {
			return PUBLICO;
		}
		return CON_SESION;
	}

	/** También puede cerrar su sesión. */
	@Override
	protected boolean permitidoSiBloqueada(String metodo, String subruta) {
		return super.permitidoSiBloqueada(metodo, subruta) || ("DELETE".equals(metodo) && subruta.isEmpty());
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if (subruta.isEmpty() && "POST".equals(metodo)) {
			iniciar(intercambio);
		} else if (subruta.isEmpty() && "GET".equals(metodo)) {
			consultar(intercambio);
		} else if (subruta.isEmpty() && "DELETE".equals(metodo)) {
			sesionController.cerrarSesion(obtenerToken(intercambio));
			enviarMensaje(intercambio, 200, "Sesión cerrada");
		} else if ("contrasena".equals(subruta) && "PUT".equals(metodo)) {
			cambiarContrasena(intercambio);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void iniciar(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "Se esperan nombreUsuario y contrasena");
			return;
		}
		SesionActiva sesion = sesionController.iniciarSesion(leerTexto(json, "nombreUsuario"),
				leerTexto(json, "contrasena"));
		if (sesion == null) {
			enviarError(intercambio, 401, "Usuario o contraseña incorrectos, o la cuenta está inactiva");
			return;
		}
		Map<String, Object> respuesta = aMapa(sesion);
		respuesta.put("token", sesion.token());
		enviarJson(intercambio, 200, JsonUtil.aJson(respuesta));
	}

	private void consultar(HttpExchange intercambio) throws IOException {
		SesionActiva sesion = obtenerSesion(intercambio);
		Map<String, Object> respuesta = aMapa(sesion);
		CuentaUsuarioDTO cuenta = sesionController.buscarCuentaDeSesion(sesion);
		respuesta.put("ultimoAcceso",
				cuenta == null || cuenta.fechaUAcceso() == null ? null : cuenta.fechaUAcceso().toString());
		enviarJson(intercambio, 200, JsonUtil.aJson(respuesta));
	}

	private void cambiarContrasena(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "Se esperan actual y nueva");
			return;
		}
		String motivo = sesionController.cambiarContrasena(obtenerSesion(intercambio), leerTexto(json, "actual"),
				leerTexto(json, "nueva"));
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		enviarMensaje(intercambio, 200, "Contraseña cambiada. Inicie sesión de nuevo");
	}

	private Map<String, Object> aMapa(SesionActiva sesion) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idCuentaU", sesion.idCuentaU());
		mapa.put("nombreUsuario", sesion.nombreUsuario());
		mapa.put("codigoRol", sesion.codigoRol());
		mapa.put("tipoDocumento", sesion.tipoDocumento());
		mapa.put("numeroDocumento", sesion.numeroDocumento());
		mapa.put("expira", sesion.expira().toString());
		mapa.put("bloqueada", sesion.bloqueada());
		return mapa;
	}
}
