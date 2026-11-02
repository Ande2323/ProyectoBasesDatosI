package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Ejemplar;

/**
 * El estado no se cambia libremente: RESERVADO y VENDIDO los ponen las reservas y
 * las salidas, y BAJA se pone con darDeBajaEjemplar. Nada se borra.
 */
public interface EjemplarDAO {

	public boolean crearEjemplar(Ejemplar e);

	/** Cambia ubicación, condición y observaciones. El precio sugerido se conserva. */
	public boolean actualizarEjemplar(Ejemplar e, int idEjemplar);

	/** Solo si está DISPONIBLE: una baja no genera salida. */
	public boolean darDeBajaEjemplar(int idEjemplar, String observacionesDanos);

	/** Los filtros en null no se aplican. */
	public List<Ejemplar> mostrarEjemplares(String estado, Integer idCarta, Integer numeroImpresion);

	public Ejemplar buscarEjemplar(int idEjemplar);

	public Ejemplar buscarEjemplarPorEtiqueta(String codigoEtiqueta);

}
