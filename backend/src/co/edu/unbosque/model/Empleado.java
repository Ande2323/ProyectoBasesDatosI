package co.edu.unbosque.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Tabla EMPLEADO (3FN). Un empleado desvinculado no se borra: queda en estado
 * RETIRADO (baja lógica) para conservar las salidas que registró.
 */
public class Empleado implements Serializable {

	private static final long serialVersionUID = 627019483L;

	private String tipoDocumento;
	private String numeroDocumento;
	private String nombres;
	private String apellidos;
	private String cargo;
	private String estado;
	private LocalDate fechaVinculacion;
	private int idCuentaU;

	public Empleado(String tipoDocumento, String numeroDocumento, String nombres, String apellidos, String cargo,
			String estado, LocalDate fechaVinculacion, int idCuentaU) {
		this.tipoDocumento = tipoDocumento;
		this.numeroDocumento = numeroDocumento;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.cargo = cargo;
		this.estado = estado;
		this.fechaVinculacion = fechaVinculacion;
		this.idCuentaU = idCuentaU;
	}

	public boolean estaActivo() {
		return "ACTIVO".equals(estado);
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDate getFechaVinculacion() {
		return fechaVinculacion;
	}

	public void setFechaVinculacion(LocalDate fechaVinculacion) {
		this.fechaVinculacion = fechaVinculacion;
	}

	public int getIdCuentaU() {
		return idCuentaU;
	}

	public void setIdCuentaU(int idCuentaU) {
		this.idCuentaU = idCuentaU;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
