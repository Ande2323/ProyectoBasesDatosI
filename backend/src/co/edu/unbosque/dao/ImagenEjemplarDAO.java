package co.edu.unbosque.dao;

import java.util.Set;

/** Las imágenes van en la base para que todos los equipos vean las mismas. */
public interface ImagenEjemplarDAO {

	/** Guarda la imagen del ejemplar y reemplaza la que tuviera. */
	public boolean guardarImagen(int idEjemplar, byte[] contenido);

	/** Retorna los bytes de la imagen, o null si el ejemplar no tiene. */
	public byte[] leerImagen(int idEjemplar);

	public boolean existeImagen(int idEjemplar);

	public Set<Integer> mostrarIdsConImagen();

}
