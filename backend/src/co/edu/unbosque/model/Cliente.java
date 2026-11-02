package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla CLIENTE (3FN). Llave (tipoDocumento, numeroDocumento): el documento
 * compuesto quedó descompuesto desde la 1FN, igual que el nombre completo.
 */
public class Cliente implements Serializable {

	private static final long serialVersionUID = 318745920L;

	private String tipoDocumento;
	private String numeroDocumento;
	private String nombres;
	private String apellidos;
	private int idCuentaU;

	public Cliente(String tipoDocumento, String numeroDocumento, String nombres, String apellidos, int idCuentaU) {
		this.tipoDocumento = tipoDocumento;
		this.numeroDocumento = numeroDocumento;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.idCuentaU = idCuentaU;
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
