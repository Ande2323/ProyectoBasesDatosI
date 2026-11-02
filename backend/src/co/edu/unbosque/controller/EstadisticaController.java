package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.EstadisticaDAO;
import co.edu.unbosque.dao.ReservaDAO;
import co.edu.unbosque.dao.impl.EstadisticaDAOImpl;
import co.edu.unbosque.dao.impl.ReservaDAOImpl;
import co.edu.unbosque.util.FechaUtil;

/** Antes de cada métrica se liberan las reservas vencidas, para que los datos sean reales. */
public class EstadisticaController {

	private EstadisticaDAO estadisticaDAO;
	private ReservaDAO reservaDAO;

	public EstadisticaController() {
		estadisticaDAO = new EstadisticaDAOImpl();
		reservaDAO = new ReservaDAOImpl();
	}

	/** Retorna la métrica pedida, o null si no existe. */
	public Object consultar(String metrica) {
		reservaDAO.vencerReservas(FechaUtil.hoy());
		return switch (metrica) {
		case "valor-inventario" -> estadisticaDAO.valorInventario();
		case "ejemplares-por-estado" -> estadisticaDAO.ejemplaresPorEstado();
		case "disponibilidad" -> estadisticaDAO.disponibilidadPorImpresion();
		case "rotacion-edicion" -> estadisticaDAO.rotacionPorEdicion();
		case "reservas-vencidas" -> estadisticaDAO.reservasVencidasPorCliente();
		case "salidas-empleado" -> estadisticaDAO.salidasPorEmpleado();
		case "reediciones" -> estadisticaDAO.reedicionesPorCarta();
		default -> null;
		};
	}

	public List<String> metricas() {
		return List.of("valor-inventario", "ejemplares-por-estado", "disponibilidad", "rotacion-edicion",
				"reservas-vencidas", "salidas-empleado", "reediciones");
	}

}
