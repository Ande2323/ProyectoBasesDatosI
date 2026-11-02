package co.edu.unbosque.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabla PRECIO_REFERENCIA (3FN). Llave (idCarta, numeroImpresion, fecha): una
 * cotización por impresión por día. El historial nunca se modifica ni se borra.
 */
public class PrecioReferencia implements Serializable {

	private static final long serialVersionUID = 471920583L;

	private int idCarta;
	private int numeroImpresion;
	private LocalDate fecha;
	private BigDecimal valor;
	private String fuenteCotizacion;

	public PrecioReferencia(int idCarta, int numeroImpresion, LocalDate fecha, BigDecimal valor,
			String fuenteCotizacion) {
		this.idCarta = idCarta;
		this.numeroImpresion = numeroImpresion;
		this.fecha = fecha;
		this.valor = valor;
		this.fuenteCotizacion = fuenteCotizacion;
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

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getFuenteCotizacion() {
		return fuenteCotizacion;
	}

	public void setFuenteCotizacion(String fuenteCotizacion) {
		this.fuenteCotizacion = fuenteCotizacion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
