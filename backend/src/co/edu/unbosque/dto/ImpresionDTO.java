package co.edu.unbosque.dto;

/**
 * La reimpresión solo indica de qué número de impresión viene: la original es
 * siempre de la misma carta, así que su idCarta es el mismo de esta impresión.
 */
public record ImpresionDTO(int idCarta, int numeroImpresion, String numeroColeccion, boolean foil, int idEdicion,
		int idIdioma, int idRareza, int idArtista, Integer originalNumeroImpresion) {

	public boolean esPrimeraEdicion() {
		return originalNumeroImpresion == null;
	}
}
