package co.edu.unbosque.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabla EJEMPLAR (3FN): la copia física. estado: DISPONIBLE, RESERVADO, VENDIDO o
 * BAJA. precioSugerido sigue al precio vigente y al factor de la condición mientras
 * la copia esté en la tienda; al quedar VENDIDO o BAJA conserva el último.
 */
public class Ejemplar implements Serializable {

	private static final long serialVersionUID = 283917465L;

	private int idEjemplar;
	private String codigoEtiqueta;
	private String ubicacion;
	private String estado;
	private BigDecimal precioSugerido;
	private LocalDate fechaIngreso;
	private String observacionesDanos;
	private int idCarta;
	private int numeroImpresion;
	private String codigoCondicion;
	private Integer consecutivoSalida;

	public Ejemplar(int idEjemplar, String codigoEtiqueta, String ubicacion, String estado, BigDecimal precioSugerido,
			LocalDate fechaIngreso, String observacionesDanos, int idCarta, int numeroImpresion,
			String codigoCondicion, Integer consecutivoSalida) {
		this.idEjemplar = idEjemplar;
		this.codigoEtiqueta = codigoEtiqueta;
		this.ubicacion = ubicacion;
		this.estado = estado;
		this.precioSugerido = precioSugerido;
		this.fechaIngreso = fechaIngreso;
		this.observacionesDanos = observacionesDanos;
		this.idCarta = idCarta;
		this.numeroImpresion = numeroImpresion;
		this.codigoCondicion = codigoCondicion;
		this.consecutivoSalida = consecutivoSalida;
	}

	public int getIdEjemplar() {
		return idEjemplar;
	}

	public void setIdEjemplar(int idEjemplar) {
		this.idEjemplar = idEjemplar;
	}

	public String getCodigoEtiqueta() {
		return codigoEtiqueta;
	}

	public void setCodigoEtiqueta(String codigoEtiqueta) {
		this.codigoEtiqueta = codigoEtiqueta;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public BigDecimal getPrecioSugerido() {
		return precioSugerido;
	}

	public void setPrecioSugerido(BigDecimal precioSugerido) {
		this.precioSugerido = precioSugerido;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public String getObservacionesDanos() {
		return observacionesDanos;
	}

	public void setObservacionesDanos(String observacionesDanos) {
		this.observacionesDanos = observacionesDanos;
	}

	public int getIdCarta() {
		return idCarta;
	}

	public void setIdCarta(int idCarta) {
		this.idCarta = idCarta;
	}

	public int getNumeroImpresion() {
		return numeroImpresion;
	}

	public void setNumeroImpresion(int numeroImpresion) {
		this.numeroImpresion = numeroImpresion;
	}

	public String getCodigoCondicion() {
		return codigoCondicion;
	}

	public void setCodigoCondicion(String codigoCondicion) {
		this.codigoCondicion = codigoCondicion;
	}

	public Integer getConsecutivoSalida() {
		return consecutivoSalida;
	}

	public void setConsecutivoSalida(Integer consecutivoSalida) {
		this.consecutivoSalida = consecutivoSalida;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
