package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla ROL (3FN). Valores fijos: CLIENTE, EMPLEADO, ADMIN. Solo lectura.
 */
public class Rol implements Serializable {

	private static final long serialVersionUID = 339749960L;

	private String codigoRol;
	private String nombre;

	public Rol(String codigoRol, String nombre) {
		this.codigoRol = codigoRol;
		this.nombre = nombre;
	}

	public String getCodigoRol() {
		return codigoRol;
	}

	public void setCodigoRol(String codigoRol) {
		this.codigoRol = codigoRol;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
