package co.edu.unbosque.dao;

import java.time.LocalDate;
import java.util.List;

import co.edu.unbosque.model.Reserva;

public interface ReservaDAO {

	/**
	 * Aparta los ejemplares en una transacción: los bloquea, confirma que sigan
	 * DISPONIBLES y los pasa a RESERVADO. Si alguno ya no está, no se aparta ninguno.
	 */
	public boolean crearReserva(Reserva r);

	/** Pasa una reserva ACTIVA a CANCELADA y libera sus ejemplares. */
	public boolean cancelarReserva(int consecutivo);

	/** Pasa una reserva ACTIVA a VENCIDA y libera sus ejemplares. */
	public boolean marcarVencida(int consecutivo);

	/** Vence las reservas ACTIVAS cuyo plazo ya pasó y libera sus ejemplares (sección H). */
	public void vencerReservas(LocalDate hoy);

	/** Si estado es null, las trae todas. */
	public List<Reserva> mostrarReservas(String estado);

	public List<Reserva> mostrarReservasDeCliente(String tipoDocumento, String numeroDocumento);

	public Reserva buscarReserva(int consecutivo);

}
