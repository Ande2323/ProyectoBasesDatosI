package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.ImpresionDTO;
import co.edu.unbosque.model.Impresion;

public class ImpresionMapper {

	public static ImpresionDTO toDTO(Impresion entidad) {
		return new ImpresionDTO(entidad.getIdCarta(), entidad.getNumeroImpresion(), entidad.getNumeroColeccion(),
				entidad.isFoil(), entidad.getIdEdicion(), entidad.getIdIdioma(), entidad.getIdRareza(),
				entidad.getIdArtista(), entidad.getOriginalNumeroImpresion());
	}

	/** La original siempre es de la misma carta: la regla pendiente de la sección 9 de la normalización. */
	public static Impresion toEntity(ImpresionDTO dto) {
		Integer originalIdCarta = dto.originalNumeroImpresion() == null ? null : dto.idCarta();
		return new Impresion(dto.idCarta(), dto.numeroImpresion(), dto.numeroColeccion(), dto.foil(),
				dto.idEdicion(), dto.idIdioma(), dto.idRareza(), dto.idArtista(), originalIdCarta,
				dto.originalNumeroImpresion());
	}

}
