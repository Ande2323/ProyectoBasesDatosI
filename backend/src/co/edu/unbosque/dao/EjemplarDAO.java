package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Ejemplar;

/**
 * El estado no se cambia libremente: RESERVADO y VENDIDO los ponen las reservas y
 * las salidas, y BAJA se pone con darDeBajaEjemplar. Nada se borra.
 */
public interface EjemplarDAO {

	public boolean crearEjemplar(Ejemplar e);

	/** Solo para deshacer un ingreso cuya imagen no se pudo guardar. Nada más borra ejemplares. */
	public void deshacerIngreso(int idEjemplar);

	/** Cambia ubicación, condición y observaciones. */
	public boolean actualizarEjemplar(Ejemplar e, int idEjemplar);

	/**
	 * Recalculan el precio sugerido (precio vigente × factor de la condición) de las
	 * copias que siguen en la tienda. Las VENDIDAS y las de BAJA conservan el suyo.
	 */
	public void recalcularPreciosDeImpresion(int idCarta, int numeroImpresion);

	public void recalcularPrecioDeEjemplar(int idEjemplar);

	public void recalcularPreciosDeCondicion(String codigoCondicion);

	/** Solo si está DISPONIBLE: una baja no genera salida. */
	public boolean darDeBajaEjemplar(int idEjemplar, String observacionesDanos);

	/** Los filtros en null no se aplican. */
	public List<Ejemplar> mostrarEjemplares(String estado, Integer idCarta, Integer numeroImpresion);

	public Ejemplar buscarEjemplar(int idEjemplar);

	public Ejemplar buscarEjemplarPorEtiqueta(String codigoEtiqueta);

}
