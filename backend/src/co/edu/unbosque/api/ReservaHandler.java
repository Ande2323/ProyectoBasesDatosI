package co.edu.unbosque.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import co.edu.unbosque.controller.ReservaController;
import co.edu.unbosque.dto.ApartadoDTO;
import co.edu.unbosque.dto.ReservaDTO;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.JsonUtil;

/**
 * Rutas de /api/reservas
 *
 * POST /api/reservas                 el cliente aparta ejemplares: {"ejemplares": [3, 4]}
 * GET  /api/reservas?estado=ACTIVA   el cliente ve las suyas; el personal, todas (con filtro)
 * GET  /api/reservas/{n}             una reserva
 * PUT  /api/reservas/{n}/cancelar    el cliente cancela la suya
 *
 * Reclamar una reserva se hace registrando la salida: POST /api/salidas. Nadie
 * marca una reserva como vencida: vence sola al pasar su plazo (sección H).
 */
public class ReservaHandler extends ManejadorBase {

	private ReservaController reservaController;

	public ReservaHandler() {
		reservaController = new ReservaController();
	}

	@Override
	protected List<String> rolesPermitidos(String metodo, String subruta) {
		if ("GET".equals(metodo)) {
			return CON_SESION;
		}
		return SOLO_CLIENTE;
	}

	@Override
	protected void atender(HttpExchange intercambio) throws IOException {
		String metodo = intercambio.getRequestMethod();
		String subruta = obtenerSubruta(intercambio);

		if (subruta.isEmpty()) {
			if ("GET".equals(metodo)) {
				mostrar(intercambio);
			} else if ("POST".equals(metodo)) {
				crear(intercambio);
			} else {
				metodoNoPermitido(intercambio);
			}
			return;
		}

		String[] partes = subruta.split("/");
		Integer consecutivo = convertirId(partes[0]);
		if (consecutivo == null || partes.length > 2) {
			enviarError(intercambio, 404, "Ruta no encontrada");
			return;
		}
		if (partes.length == 1) {
			if ("GET".equals(metodo)) {
				buscar(intercambio, consecutivo);
			} else {
				metodoNoPermitido(intercambio);
			}
		} else if ("PUT".equals(metodo) && "cancelar".equals(partes[1])) {
			responder(intercambio, consecutivo,
					reservaController.cancelarReserva(consecutivo, obtenerSesion(intercambio)));
		} else {
			enviarError(intercambio, 404, "Ruta no encontrada");
		}
	}

	private void mostrar(HttpExchange intercambio) throws IOException {
		String estado = obtenerParametro(intercambio, "estado");
		SesionActiva sesion = obtenerSesion(intercambio);
		List<Object> lista = new ArrayList<>();
		for (ReservaDTO dto : reservaController.mostrarReservas(sesion, estado == null ? null : estado.trim().toUpperCase())) {
			lista.add(aMapa(dto));
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(lista));
	}

	private void buscar(HttpExchange intercambio, int consecutivo) throws IOException {
		ReservaDTO dto = reservaController.buscarReserva(consecutivo, obtenerSesion(intercambio));
		if (dto == null) {
			enviarError(intercambio, 404, "No existe la reserva " + consecutivo);
			return;
		}
		enviarJson(intercambio, 200, JsonUtil.aJson(aMapa(dto)));
	}

	private void crear(HttpExchange intercambio) throws IOException {
		Map<String, Object> json = JsonUtil.leerObjeto(leerCuerpo(intercambio));
		List<Integer> ejemplares = json == null ? null : leerListaEnteros(json, "ejemplares");
		if (ejemplares == null) {
			enviarError(intercambio, 400, "ejemplares debe ser una lista con los id de los ejemplares");
			return;
		}
		String motivo = reservaController.validarReserva(ejemplares);
		if (motivo != null) {
			enviarError(intercambio, motivo.startsWith("El ejemplar") ? 409 : 400, motivo);
			return;
		}
		ReservaDTO creada = reservaController.crearReserva(ejemplares, obtenerSesion(intercambio));
		if (creada == null) {
			enviarError(intercambio, 409, "Alguno de los ejemplares acaba de ser apartado: no se apartó ninguno");
			return;
		}
		enviarJson(intercambio, 201, JsonUtil.aJson(aMapa(creada)));
	}

	private void responder(HttpExchange intercambio, int consecutivo, String motivo) throws IOException {
		if (motivo != null) {
			enviarError(intercambio, motivo.startsWith("No existe") ? 404 : 400, motivo);
			return;
		}
		enviarJson(intercambio, 200,
				JsonUtil.aJson(aMapa(reservaController.buscarReserva(consecutivo, obtenerSesion(intercambio)))));
	}

	private Map<String, Object> aMapa(ReservaDTO dto) {
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("consecutivo", dto.consecutivo());
		mapa.put("fechaCreacion", dto.fechaCreacion().toString());
		mapa.put("fechaVencimiento", dto.fechaVencimiento() == null ? null : dto.fechaVencimiento().toString());
		mapa.put("estado", dto.estado());
		mapa.put("clienteTipoDocumento", dto.clienteTipoDocumento());
		mapa.put("clienteNumeroDocumento", dto.clienteNumeroDocumento());
		mapa.put("consecutivoSalida", dto.consecutivoSalida());
		mapa.put("ejemplares", new ArrayList<Object>(dto.ejemplares()));
		List<Object> apartados = new ArrayList<>();
		for (ApartadoDTO apartado : reservaController.mostrarApartados(dto.consecutivo())) {
			Map<String, Object> fila = new LinkedHashMap<>();
			fila.put("idEjemplar", apartado.idEjemplar());
			fila.put("codigoEtiqueta", apartado.codigoEtiqueta());
			fila.put("idCarta", apartado.idCarta());
			fila.put("nombreCarta", apartado.nombreCarta());
			fila.put("codigoCondicion", apartado.codigoCondicion());
			fila.put("precioSugerido", apartado.precioSugerido());
			apartados.add(fila);
		}
		mapa.put("apartados", apartados);
		return mapa;
	}
}
