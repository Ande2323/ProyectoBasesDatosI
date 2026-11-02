package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla TIPO (3FN). nivel: 1 supertipo, 2 tipo, 3 subtipo.
 */
public class Tipo implements Serializable {

	private static final long serialVersionUID = 443043868L;

	private int idTipo;
	private int nivel;
	private String codigo;
	private String nombre;

	public Tipo(int idTipo, int nivel, String codigo, String nombre) {
		this.idTipo = idTipo;
		this.nivel = nivel;
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public int getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
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
