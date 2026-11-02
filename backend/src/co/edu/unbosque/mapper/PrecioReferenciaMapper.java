package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.PrecioReferenciaDTO;
import co.edu.unbosque.model.PrecioReferencia;

public class PrecioReferenciaMapper {

	public static PrecioReferenciaDTO toDTO(PrecioReferencia entidad) {
		return new PrecioReferenciaDTO(entidad.getIdCarta(), entidad.getNumeroImpresion(), entidad.getFecha(),
				entidad.getValor(), entidad.getFuenteCotizacion());
	}

	public static PrecioReferencia toEntity(PrecioReferenciaDTO dto) {
		return new PrecioReferencia(dto.idCarta(), dto.numeroImpresion(), dto.fecha(), dto.valor(),
				dto.fuenteCotizacion());
	}

}
