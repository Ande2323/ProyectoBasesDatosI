package co.edu.unbosque.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Tabla EDICION (3FN): colección identificada por el código impreso en la carta.
 */
public class Edicion implements Serializable {

	private static final long serialVersionUID = 639572182L;

	private int idEdicion;
	private String codigoImpreso;
	private String nombre;
	private LocalDate fechaLanzamiento;
	private Integer cantidadCartas;
	private String tipoEdicion;

	public Edicion(int idEdicion, String codigoImpreso, String nombre, LocalDate fechaLanzamiento, Integer cantidadCartas, String tipoEdicion) {
		this.idEdicion = idEdicion;
		this.codigoImpreso = codigoImpreso;
		this.nombre = nombre;
		this.fechaLanzamiento = fechaLanzamiento;
		this.cantidadCartas = cantidadCartas;
		this.tipoEdicion = tipoEdicion;
	}

	public int getIdEdicion() {
		return idEdicion;
	}

	public void setIdEdicion(int idEdicion) {
		this.idEdicion = idEdicion;
	}

	public String getCodigoImpreso() {
		return codigoImpreso;
	}

	public void setCodigoImpreso(String codigoImpreso) {
		this.codigoImpreso = codigoImpreso;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaLanzamiento() {
		return fechaLanzamiento;
	}

	public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
		this.fechaLanzamiento = fechaLanzamiento;
	}

	public Integer getCantidadCartas() {
		return cantidadCartas;
	}

	public void setCantidadCartas(Integer cantidadCartas) {
		this.cantidadCartas = cantidadCartas;
	}

	public String getTipoEdicion() {
		return tipoEdicion;
	}

	public void setTipoEdicion(String tipoEdicion) {
		this.tipoEdicion = tipoEdicion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
