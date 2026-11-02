package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.ClienteController;
import co.edu.unbosque.dto.ClienteDTO;
import co.edu.unbosque.dto.RegistroClienteDTO;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/clientes
 *
 * POST /api/clientes                 registro desde la página (sin sesión)
 * GET  /api/clientes                 lista (empleados y administrador)
 * GET  /api/clientes/{tipo}/{numero} un cliente (el personal, o el propio cliente)
 * PUT  /api/clientes/{tipo}/{numero} cambia nombres y apellidos (el propio cliente o el administrador)
 *
 * Un cliente nunca ve los datos de otro cliente (sección G).
 */
public class ClienteHandler extends ManejadorBase {

	private ClienteController clienteController;

	public ClienteHandler() {
		clienteController = new ClienteController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("POST".equals(metodo)) {
			return PUBLICO;
		}
		if ("GET".equals(metodo) && subruta.isEmpty()) {
			return PERSONAL;
		}
		return CON_SESION;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if (subruta.isEmpty()) {
			if ("POST".equals(metodo)) {
				registrar(intercambio);
			} else if ("GET".equals(metodo)) {
				mostrar(intercambio);
			} else {
				metodoNoPermitido(intercambio);
			}
			return;
		}

		String[] partes = subruta.split("/");
		if (partes.length != 2) {
			enviarError(intercambio, 404, "Ruta no encontrada: use /api/clientes/{tipoDocumento}/{numeroDocumento}");
			return;
		}
		String tipo = partes[0].toUpperCase();
		String numero = partes[1].toUpperCase();
		SesionActiva sesion = obtenerSesion(intercambio);
		boolean esElMismo = sesion.esCliente() && tipo.equals(sesion.tipoDocumento())
				&& numero.equals(sesion.numeroDocumento());

		if ("GET".equals(metodo)) {
			if (sesion.esCliente() && !esElMismo) {
				enviarError(intercambio, 403, "Un cliente solo puede consultar sus propios datos");
				return;
			}
			buscar(intercambio, tipo, numero);
		} else if ("PUT".equals(metodo)) {
			if (!esElMismo && !sesion.esAdmin()) {
				enviarError(intercambio, 403, "Solo el propio cliente o el administrador pueden cambiar estos datos");
				return;
			}
			actualizar(intercambio, tipo, numero);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void registrar(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		RegistroClienteDTO dto = new RegistroClienteDTO(leerTexto(json, "tipoDocumento"),
				leerTexto(json, "numeroDocumento"), leerTexto(json, "nombres"), leerTexto(json, "apellidos"),
				leerTexto(json, "nombreUsuario"), leerTexto(json, "contrasena"));
		String motivo = clienteController.validarRegistro(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		ClienteDTO creado = clienteController.registrarCliente(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo completar el registro: el documento o el usuario ya existen");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (ClienteDTO dto : clienteController.mostrarClientes()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, String tipo, String numero) throws IOException {
		ClienteDTO dto = clienteController.buscarCliente(tipo, numero);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el cliente " + tipo + " " + numero);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void actualizar(HttpExchange intercambio, String tipo, String numero) throws IOException {
		if (clienteController.buscarCliente(tipo, numero) == null) {
			enviarError(intercambio, 404, "No existe el cliente " + tipo + " " + numero);
			return;
		}
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		ClienteDTO dto = new ClienteDTO(tipo, numero, leerTexto(json, "nombres"), leerTexto(json, "apellidos"), 0);
		if (!clienteController.actualizarCliente(dto, tipo, numero)) {
			enviarError(intercambio, 400, "Nombres y apellidos son obligatorios (máximo 100 caracteres cada uno)");
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(clienteController.buscarCliente(tipo, numero))));
	}

	private Map<String, Object> aMapa(ClienteDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("tipoDocumento", dto.tipoDocumento());
		mapa.put("numeroDocumento", dto.numeroDocumento());
		mapa.put("nombres", dto.nombres());
		mapa.put("apellidos", dto.apellidos());
		mapa.put("idCuentaU", dto.idCuentaU());
		return mapa;
	}
}
