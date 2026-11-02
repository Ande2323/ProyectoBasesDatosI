package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla RAREZA (3FN). La rareza es de la impresión, no de la carta.
 */
public class Rareza implements Serializable {

	private static final long serialVersionUID = 794923749L;

	private int idRareza;
	private String codigo;
	private String nombre;

	public Rareza(int idRareza, String codigo, String nombre) {
		this.idRareza = idRareza;
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public int getIdRareza() {
		return idRareza;
	}

	public void setIdRareza(int idRareza) {
		this.idRareza = idRareza;
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
