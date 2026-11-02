package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.EjemplarDTO;
import co.edu.unbosque.model.Ejemplar;

public class EjemplarMapper {

	public static EjemplarDTO toDTO(Ejemplar entidad) {
		return new EjemplarDTO(entidad.getIdEjemplar(), entidad.getCodigoEtiqueta(), entidad.getUbicacion(),
				entidad.getEstado(), entidad.getPrecioSugerido(), entidad.getFechaIngreso(),
				entidad.getObservacionesDanos(), entidad.getIdCarta(), entidad.getNumeroImpresion(),
				entidad.getCodigoCondicion(), entidad.getConsecutivoSalida());
	}

	public static Ejemplar toEntity(EjemplarDTO dto) {
		return new Ejemplar(dto.idEjemplar(), dto.codigoEtiqueta(), dto.ubicacion(), dto.estado(), dto.precioSugerido(),
				dto.fechaIngreso(), dto.observacionesDanos(), dto.idCarta(), dto.numeroImpresion(),
				dto.codigoCondicion(), dto.consecutivoSalida());
	}

}
