package co.edu.unbosque.model;

import java.io.Serializable;

/**
 * Tabla IMPRESION (3FN): aparición de una carta en una edición, idioma y
 * acabado. Llave (idCarta, numeroImpresion). Si originalIdCarta y
 * originalNumeroImpresion están vacíos, es una primera edición; si no, es una
 * reimpresión (relación REIMPRIMIR).
 */
public class Impresion implements Serializable {

	private static final long serialVersionUID = 562839104L;

	private int idCarta;
	private int numeroImpresion;
	private String numeroColeccion;
	private boolean foil;
	private int idEdicion;
	private int idIdioma;
	private int idRareza;
	private int idArtista;
	private Integer originalIdCarta;
	private Integer originalNumeroImpresion;

	public Impresion(int idCarta, int numeroImpresion, String numeroColeccion, boolean foil, int idEdicion,
			int idIdioma, int idRareza, int idArtista, Integer originalIdCarta, Integer originalNumeroImpresion) {
		this.idCarta = idCarta;
		this.numeroImpresion = numeroImpresion;
		this.numeroColeccion = numeroColeccion;
		this.foil = foil;
		this.idEdicion = idEdicion;
		this.idIdioma = idIdioma;
		this.idRareza = idRareza;
		this.idArtista = idArtista;
		this.originalIdCarta = originalIdCarta;
		this.originalNumeroImpresion = originalNumeroImpresion;
	}

	public boolean esPrimeraEdicion() {
		return originalNumeroImpresion == null;
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

	public String getNumeroColeccion() {
		return numeroColeccion;
	}

	public void setNumeroColeccion(String numeroColeccion) {
		this.numeroColeccion = numeroColeccion;
	}

	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	public int getIdEdicion() {
		return idEdicion;
	}

	public void setIdEdicion(int idEdicion) {
		this.idEdicion = idEdicion;
	}

	public int getIdIdioma() {
		return idIdioma;
	}

	public void setIdIdioma(int idIdioma) {
		this.idIdioma = idIdioma;
	}

	public int getIdRareza() {
		return idRareza;
	}

	public void setIdRareza(int idRareza) {
		this.idRareza = idRareza;
	}

	public int getIdArtista() {
		return idArtista;
	}

	public void setIdArtista(int idArtista) {
		this.idArtista = idArtista;
	}

	public Integer getOriginalIdCarta() {
		return originalIdCarta;
	}

	public void setOriginalIdCarta(Integer originalIdCarta) {
		this.originalIdCarta = originalIdCarta;
	}

	public Integer getOriginalNumeroImpresion() {
		return originalNumeroImpresion;
	}

	public void setOriginalNumeroImpresion(Integer originalNumeroImpresion) {
		this.originalNumeroImpresion = originalNumeroImpresion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
