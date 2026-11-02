package co.edu.unbosque.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla CARTA (3FN) junto con sus relaciones N:M POSEER (carta_color) y
 * PERTENECER (carta_tipo), que se guardan siempre junto con la carta.
 * valor_mana no es atributo: se calcula con ManaUtil.
 */
public class Carta implements Serializable {

	private static final long serialVersionUID = 735190284L;

	private int idCarta;
	private String nombreOficial;
	private String textoOraculo;
	private String costeMana;
	private String poder;
	private String resistencia;
	private String lealtad;
	private ArrayList<Integer> colores;
	private ArrayList<Integer> tipos;

	public Carta(int idCarta, String nombreOficial, String textoOraculo, String costeMana, String poder,
			String resistencia, String lealtad, List<Integer> colores, List<Integer> tipos) {
		this.idCarta = idCarta;
		this.nombreOficial = nombreOficial;
		this.textoOraculo = textoOraculo;
		this.costeMana = costeMana;
		this.poder = poder;
		this.resistencia = resistencia;
		this.lealtad = lealtad;
		this.colores = colores == null ? new ArrayList<>() : new ArrayList<>(colores);
		this.tipos = tipos == null ? new ArrayList<>() : new ArrayList<>(tipos);
	}

	public int getIdCarta() {
		return idCarta;
	}

	public void setIdCarta(int idCarta) {
		this.idCarta = idCarta;
	}

	public String getNombreOficial() {
		return nombreOficial;
	}

	public void setNombreOficial(String nombreOficial) {
		this.nombreOficial = nombreOficial;
	}

	public String getTextoOraculo() {
		return textoOraculo;
	}

	public void setTextoOraculo(String textoOraculo) {
		this.textoOraculo = textoOraculo;
	}

	public String getCosteMana() {
		return costeMana;
	}

	public void setCosteMana(String costeMana) {
		this.costeMana = costeMana;
	}

	public String getPoder() {
		return poder;
	}

	public void setPoder(String poder) {
		this.poder = poder;
	}

	public String getResistencia() {
		return resistencia;
	}

	public void setResistencia(String resistencia) {
		this.resistencia = resistencia;
	}

	public String getLealtad() {
		return lealtad;
	}

	public void setLealtad(String lealtad) {
		this.lealtad = lealtad;
	}

	public List<Integer> getColores() {
		return colores;
	}

	public void setColores(List<Integer> colores) {
		this.colores = colores == null ? new ArrayList<>() : new ArrayList<>(colores);
	}

	public List<Integer> getTipos() {
		return tipos;
	}

	public void setTipos(List<Integer> tipos) {
		this.tipos = tipos == null ? new ArrayList<>() : new ArrayList<>(tipos);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
