package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla CONDICION (3FN): estado de conservación de un ejemplar y el factor
 * que se aplica sobre el precio de referencia.
 */
public class Condicion implements Serializable {

	private static final long serialVersionUID = 481203947L;

	private String codigoCondicion;
	private String nombre;
	private double factorAjuste;

	public Condicion(String codigoCondicion, String nombre, double factorAjuste) {
		this.codigoCondicion = codigoCondicion;
		this.nombre = nombre;
		this.factorAjuste = factorAjuste;
	}

	public String getCodigoCondicion() {
		return codigoCondicion;
	}

	public void setCodigoCondicion(String codigoCondicion) {
		this.codigoCondicion = codigoCondicion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getFactorAjuste() {
		return factorAjuste;
	}

	public void setFactorAjuste(double factorAjuste) {
		this.factorAjuste = factorAjuste;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
