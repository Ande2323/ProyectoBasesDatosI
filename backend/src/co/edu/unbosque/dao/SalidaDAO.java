package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Salida;

public interface SalidaDAO {

	/** Entrega directa: los ejemplares deben seguir DISPONIBLES. Todo en una transacción. */
	public boolean registrarEntregaDirecta(Salida s);

	/**
	 * Reclamo de reserva (s.getConsecutivoReserva()): los ejemplares de la salida
	 * pasan a VENDIDO, los que no se recogieron vuelven a DISPONIBLE y la reserva
	 * queda ENTREGADA. Todo en una transacción.
	 */
	public boolean registrarReclamo(Salida s);

	public List<Salida> mostrarSalidas();

	public Salida buscarSalida(int consecutivoSalida);

}
