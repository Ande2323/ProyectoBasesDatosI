package co.edu.unbosque.api;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.EmpleadoController;
import co.edu.unbosque.dto.EmpleadoDTO;
import co.edu.unbosque.dto.RegistroEmpleadoDTO;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/empleados (solo administrador).
 *
 * GET    /api/empleados                 lista
 * GET    /api/empleados/{tipo}/{numero} uno
 * POST   /api/empleados                 registra empleado y su cuenta
 * PUT    /api/empleados/{tipo}/{numero} cambia nombres, apellidos, cargo y fecha de vinculación
 * DELETE /api/empleados/{tipo}/{numero} baja lógica: empleado RETIRADO y cuenta INACTIVA
 */
public class EmpleadoHandler extends ManejadorBase {

	private EmpleadoController empleadoController;

	public EmpleadoHandler() {
		empleadoController = new EmpleadoController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		return SOLO_ADMIN;
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

		String[] partes = subruta.split("/");
		if (partes.length != 2) {
			enviarError(intercambio, 404, "Ruta no encontrada: use /api/empleados/{tipoDocumento}/{numeroDocumento}");
			return;
		}
		String tipo = partes[0].toUpperCase();
		String numero = partes[1].toUpperCase();
		if ("GET".equals(metodo)) {
			buscar(intercambio, tipo, numero);
		} else if ("PUT".equals(metodo)) {
			actualizar(intercambio, tipo, numero);
		} else if ("DELETE".equals(metodo)) {
			darDeBaja(intercambio, tipo, numero);
		} else {
			metodoNoPermitido(intercambio);
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		List<Object> lista = new ArrayList<>();
		for (EmpleadoDTO dto : empleadoController.mostrarEmpleados()) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, String tipo, String numero) throws IOException {
		EmpleadoDTO dto = empleadoController.buscarEmpleado(tipo, numero);
		if (dto == null) {
			enviarError(intercambio, 404, "No existe el empleado " + tipo + " " + numero);
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
		LocalDate fecha;
		try {
			fecha = leerFecha(json, "fechaVinculacion");
		} catch (DateTimeParseException e) {
			enviarError(intercambio, 400, "fechaVinculacion debe venir como AAAA-MM-DD");
			return;
		}
		RegistroEmpleadoDTO dto = new RegistroEmpleadoDTO(leerTexto(json, "tipoDocumento"),
				leerTexto(json, "numeroDocumento"), leerTexto(json, "nombres"), leerTexto(json, "apellidos"),
				leerTexto(json, "cargo"), fecha, leerTexto(json, "nombreUsuario"), leerTexto(json, "contrasena"),
				leerTexto(json, "codigoRol"));
		String motivo = empleadoController.validarRegistro(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		EmpleadoDTO creado = empleadoController.registrarEmpleado(dto);
		if (creado == null) {
			enviarError(intercambio, 400, "No se pudo registrar: el documento o el usuario ya existen");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creado)));
	}

	private void actualizar(HttpExchange intercambio, String tipo, String numero) throws IOException {
		if (empleadoController.buscarEmpleado(tipo, numero) == null) {
			enviarError(intercambio, 404, "No existe el empleado " + tipo + " " + numero);
			return;
		}
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		if (json == null) {
			enviarError(intercambio, 400, "JSON mal formado");
			return;
		}
		LocalDate fecha;
		try {
			fecha = leerFecha(json, "fechaVinculacion");
		} catch (DateTimeParseException e) {
			enviarError(intercambio, 400, "fechaVinculacion debe venir como AAAA-MM-DD");
			return;
		}
		EmpleadoDTO dto = new EmpleadoDTO(tipo, numero, leerTexto(json, "nombres"), leerTexto(json, "apellidos"),
				leerTexto(json, "cargo"), null, fecha, 0);
		String motivo = empleadoController.validarDatos(dto);
		if (motivo != null) {
			enviarError(intercambio, 400, motivo);
			return;
		}
		if (!empleadoController.actualizarEmpleado(dto, tipo, numero)) {
			enviarError(intercambio, 400, "No se pudo actualizar el empleado");
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(empleadoController.buscarEmpleado(tipo, numero))));
	}

	private void darDeBaja(HttpExchange intercambio, String tipo, String numero) throws IOException {
		String motivo = empleadoController.darDeBajaEmpleado(tipo, numero, obtenerSesion(intercambio));
		if (motivo != null) {
			enviarError(intercambio, motivo.startsWith("No existe") ? 404 : 400, motivo);
			return;
		}
		enviarMensaje(intercambio, 200, "Empleado " + tipo + " " + numero + " dado de baja");
	}

	/** null si no viene; lanza DateTimeParseException si viene mal escrita. */
	private LocalDate leerFecha(Map<String, Object> json, String clave) {
		String texto = leerTexto(json, clave);
		return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim());
	}

	private Map<String, Object> aMapa(EmpleadoDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("tipoDocumento", dto.tipoDocumento());
		mapa.put("numeroDocumento", dto.numeroDocumento());
		mapa.put("nombres", dto.nombres());
		mapa.put("apellidos", dto.apellidos());
		mapa.put("cargo", dto.cargo());
		mapa.put("estado", dto.estado());
		mapa.put("fechaVinculacion", dto.fechaVinculacion() == null ? null : dto.fechaVinculacion().toString());
		mapa.put("idCuentaU", dto.idCuentaU());
		return mapa;
	}
}
