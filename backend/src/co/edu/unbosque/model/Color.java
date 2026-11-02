package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla COLOR (3FN): colores de Magic con su orden canónico.
 */
public class Color implements Serializable {

	private static final long serialVersionUID = 227904006L;

	private int idColor;
	private String nombreColor;
	private Integer ordenCanonico;

	public Color(int idColor, String nombreColor, Integer ordenCanonico) {
		this.idColor = idColor;
		this.nombreColor = nombreColor;
		this.ordenCanonico = ordenCanonico;
	}

	public int getIdColor() {
		return idColor;
	}

	public void setIdColor(int idColor) {
		this.idColor = idColor;
	}

	public String getNombreColor() {
		return nombreColor;
	}

	public void setNombreColor(String nombreColor) {
		this.nombreColor = nombreColor;
	}

	public Integer getOrdenCanonico() {
		return ordenCanonico;
	}

	public void setOrdenCanonico(Integer ordenCanonico) {
		this.ordenCanonico = ordenCanonico;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
