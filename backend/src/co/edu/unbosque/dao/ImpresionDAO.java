package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Impresion;

public interface ImpresionDAO {

	public boolean crearImpresion(Impresion i);

	public boolean actualizarImpresion(Impresion i, int idCarta, int numeroImpresion);

	public boolean eliminarImpresion(int idCarta, int numeroImpresion);

	public List<Impresion> mostrarImpresiones();

	public List<Impresion> mostrarImpresionesDeCarta(int idCarta);

	public List<Impresion> mostrarReimpresiones(int idCarta, int numeroImpresion);

	public Impresion buscarImpresion(int idCarta, int numeroImpresion);

	/** La impresión física: misma edición, idioma, número de colección y acabado. */
	public Impresion buscarImpresionFisica(int idEdicion, int idIdioma, String numeroColeccion, boolean foil);

}
