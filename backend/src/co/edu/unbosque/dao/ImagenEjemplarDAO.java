package co.edu.unbosque.dao;

/** Las imágenes no van en la base: son archivos en una carpeta, uno por ejemplar. */
public interface ImagenEjemplarDAO {

	/** Guarda la imagen del ejemplar y reemplaza la que tuviera. */
	public boolean guardarImagen(int idEjemplar, byte[] contenido, String extension);

	/** Retorna los bytes de la imagen, o null si el ejemplar no tiene. */
	public byte[] leerImagen(int idEjemplar);

	public String buscarExtension(int idEjemplar);

}
