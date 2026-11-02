package co.edu.unbosque.dao;

import java.time.LocalDate;
import java.util.List;

import co.edu.unbosque.model.PrecioReferencia;

/** Sin actualizar ni eliminar: el historial de precios se conserva completo (sección E). */
public interface PrecioReferenciaDAO {

	public boolean registrarPrecio(PrecioReferencia p);

	public List<PrecioReferencia> mostrarHistorial(int idCarta, int numeroImpresion);

	public PrecioReferencia buscarPrecio(int idCarta, int numeroImpresion, LocalDate fecha);

	/** El precio vigente es el de la fecha más reciente. */
	public PrecioReferencia buscarPrecioVigente(int idCarta, int numeroImpresion);

}
