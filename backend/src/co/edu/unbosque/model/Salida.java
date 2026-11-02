package co.edu.unbosque.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla SALIDA (3FN) junto con los ejemplares que entrega (relación RETIRAR) y la
 * reserva que la originó, si la hay (relación ORIGINAR). No registra dinero.
 */
public class Salida implements Serializable {

	private static final long serialVersionUID = 136482957L;

	private int consecutivoSalida;
	private String motivo;
	private String observaciones;
	private LocalDate fecha;
	private String clienteTipoDocumento;
	private String clienteNumeroDocumento;
	private String empleadoTipoDocumento;
	private String empleadoNumeroDocumento;
	private ArrayList<Integer> ejemplares;
	private Integer consecutivoReserva;

	public Salida(int consecutivoSalida, String motivo, String observaciones, LocalDate fecha,
			String clienteTipoDocumento, String clienteNumeroDocumento, String empleadoTipoDocumento,
			String empleadoNumeroDocumento, List<Integer> ejemplares, Integer consecutivoReserva) {
		this.consecutivoSalida = consecutivoSalida;
		this.motivo = motivo;
		this.observaciones = observaciones;
		this.fecha = fecha;
		this.clienteTipoDocumento = clienteTipoDocumento;
		this.clienteNumeroDocumento = clienteNumeroDocumento;
		this.empleadoTipoDocumento = empleadoTipoDocumento;
		this.empleadoNumeroDocumento = empleadoNumeroDocumento;
		this.ejemplares = ejemplares == null ? new ArrayList<>() : new ArrayList<>(ejemplares);
		this.consecutivoReserva = consecutivoReserva;
	}

	public int getConsecutivoSalida() {
		return consecutivoSalida;
	}

	public void setConsecutivoSalida(int consecutivoSalida) {
		this.consecutivoSalida = consecutivoSalida;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getClienteTipoDocumento() {
		return clienteTipoDocumento;
	}

	public void setClienteTipoDocumento(String clienteTipoDocumento) {
		this.clienteTipoDocumento = clienteTipoDocumento;
	}

	public String getClienteNumeroDocumento() {
		return clienteNumeroDocumento;
	}

	public void setClienteNumeroDocumento(String clienteNumeroDocumento) {
		this.clienteNumeroDocumento = clienteNumeroDocumento;
	}

	public String getEmpleadoTipoDocumento() {
		return empleadoTipoDocumento;
	}

	public void setEmpleadoTipoDocumento(String empleadoTipoDocumento) {
		this.empleadoTipoDocumento = empleadoTipoDocumento;
	}

	public String getEmpleadoNumeroDocumento() {
		return empleadoNumeroDocumento;
	}

	public void setEmpleadoNumeroDocumento(String empleadoNumeroDocumento) {
		this.empleadoNumeroDocumento = empleadoNumeroDocumento;
	}

	public List<Integer> getEjemplares() {
		return ejemplares;
	}

	public void setEjemplares(List<Integer> ejemplares) {
		this.ejemplares = ejemplares == null ? new ArrayList<>() : new ArrayList<>(ejemplares);
	}

	public Integer getConsecutivoReserva() {
		return consecutivoReserva;
	}

	public void setConsecutivoReserva(Integer consecutivoReserva) {
		this.consecutivoReserva = consecutivoReserva;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
