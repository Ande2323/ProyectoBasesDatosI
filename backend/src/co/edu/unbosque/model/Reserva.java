package co.edu.unbosque.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla RESERVA (3FN) junto con sus ejemplares (relación APARTAR, tabla
 * ejemplar_reserva). estado: ACTIVA, VENCIDA, CANCELADA o ENTREGADA.
 * fechaVencimiento se fija al crear la reserva (sección 7.2 de la normalización).
 */
public class Reserva implements Serializable {

	private static final long serialVersionUID = 750294618L;

	private int consecutivo;
	private LocalDate fechaCreacion;
	private String estado;
	private LocalDate fechaVencimiento;
	private String clienteTipoDocumento;
	private String clienteNumeroDocumento;
	private Integer consecutivoSalida;
	private ArrayList<Integer> ejemplares;

	public Reserva(int consecutivo, LocalDate fechaCreacion, String estado, LocalDate fechaVencimiento,
			String clienteTipoDocumento, String clienteNumeroDocumento, Integer consecutivoSalida,
			List<Integer> ejemplares) {
		this.consecutivo = consecutivo;
		this.fechaCreacion = fechaCreacion;
		this.estado = estado;
		this.fechaVencimiento = fechaVencimiento;
		this.clienteTipoDocumento = clienteTipoDocumento;
		this.clienteNumeroDocumento = clienteNumeroDocumento;
		this.consecutivoSalida = consecutivoSalida;
		this.ejemplares = ejemplares == null ? new ArrayList<>() : new ArrayList<>(ejemplares);
	}

	public int getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
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

	public Integer getConsecutivoSalida() {
		return consecutivoSalida;
	}

	public void setConsecutivoSalida(Integer consecutivoSalida) {
		this.consecutivoSalida = consecutivoSalida;
	}

	public List<Integer> getEjemplares() {
		return ejemplares;
	}

	public void setEjemplares(List<Integer> ejemplares) {
		this.ejemplares = ejemplares == null ? new ArrayList<>() : new ArrayList<>(ejemplares);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
