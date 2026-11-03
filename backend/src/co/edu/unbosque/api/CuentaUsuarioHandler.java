package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.CuentaUsuarioController;
import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/cuentas (solo administrador). La contraseña nunca se devuelve.
 *
 * GET /api/cuentas               lista todas las cuentas
 * GET /api/cuentas/{id}          una cuenta
 * PUT /api/cuentas/{id}/estado   {"estado": "INACTIVA"} da de baja para siempre; "BLOQUEADA" bloquea; "ACTIVA" desbloquea
 * PUT /api/cuentas/{id}/rol      {"codigoRol": "ADM"} asigna rol a una cuenta de empleado
 */
public class CuentaUsuarioHandler extends ManejadorBase {

	private CuentaUsuarioController cuentaUsuarioController;

	public CuentaUsuarioHandler() {
		cuentaUsuarioController = new CuentaUsuarioController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return SOLO_ADMIN;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);
		String[] partes = subruta.isEmpty() ? new String[0] : subruta.split("/");

		if (partes.length == 0 && "GET".equals(metodo)) {
			mostrar(intercambio);
			return;
		}
		if (partes.length == 0) {
			metodoNoPermitido(intercambio);
			return;
		}
		Integer idCuentaU = convertirId(partes[0]);
		if (idCuentaU == null) {
			enviarError(intercambio, 400, "El id de la cuenta debe ser un número entero");
			return;
		}
		if (partes.length == 1 && "GET".equals(metodo)) {
			buscar(intercambio, idCuentaU);
		} else if (partes.length == 2 && "PUT".equals(metodo)
				&& ("estado".equals(partes[1]) || "rol".equals(partes[1]))) {
			cambiar(intercambio, idCuentaU, partes[1]);
		} else if (partes.length > 2 || (partes.length == 2 && !"PUT".equals(metodo))) {
			enviarError(intercambio, 404, "Ruta no encontrada");
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (CuentaUsuarioDTO dto : cuentaUsuarioController.mostrarCuentas()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, int idCuentaU) throws IOException {
		CuentaUsuarioDTO dto = cuentaUsuarioController.buscarCuenta(idCuentaU);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la cuenta " + idCuentaU);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void cambiar(HttpExchange intercambio, int idCuentaU, String que) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		String motivo;
		if ("estado".equals(que)) {
			motivo = cuentaUsuarioController.cambiarEstado(idCuentaU, leerTexto(json, "estado"),
					obtenerSesion(intercambio));
		} else {
			motivo = cuentaUsuarioController.cambiarRol(idCuentaU, leerTexto(json, "codigoRol"),
					obtenerSesion(intercambio));
		}
		if (motivo != null) {
			enviarError(intercambio, motivo.startsWith("No existe") ? 404 : 400, motivo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(cuentaUsuarioController.buscarCuenta(idCuentaU))));
	}

	private Map<String, Object> aMapa(CuentaUsuarioDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("idCuentaU", dto.idCuentaU());
		mapa.put("nombreUsuario", dto.nombreUsuario());
		mapa.put("estado", dto.estado());
		mapa.put("codigoRol", dto.codigoRol());
		mapa.put("ultimoAcceso", dto.fechaUAcceso() == null ? null : dto.fechaUAcceso().toString());
		return mapa;
	}
}
