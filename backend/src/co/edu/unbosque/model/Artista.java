package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla ARTISTA (3FN). Id interno: no existe autoridad que identifique ilustradores.
 */
public class Artista implements Serializable {

	private static final long serialVersionUID = 210205473L;

	private int idArtista;
	private String nombre;

	public Artista(int idArtista, String nombre) {
		this.idArtista = idArtista;
		this.nombre = nombre;
	}

	public int getIdArtista() {
		return idArtista;
	}

	public void setIdArtista(int idArtista) {
		this.idArtista = idArtista;
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
