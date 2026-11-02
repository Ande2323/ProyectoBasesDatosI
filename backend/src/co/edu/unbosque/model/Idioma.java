package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla IDIOMA (3FN).
 */
public class Idioma implements Serializable {

	private static final long serialVersionUID = 649565454L;

	private int idIdioma;
	private String codigo;
	private String nombre;

	public Idioma(int idIdioma, String codigo, String nombre) {
		this.idIdioma = idIdioma;
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public int getIdIdioma() {
		return idIdioma;
	}

	public void setIdIdioma(int idIdioma) {
		this.idIdioma = idIdioma;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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
