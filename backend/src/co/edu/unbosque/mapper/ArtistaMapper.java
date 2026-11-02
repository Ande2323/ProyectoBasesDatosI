package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.ArtistaDTO;
import co.edu.unbosque.model.Artista;

public class ArtistaMapper {

	public static ArtistaDTO toDTO(Artista entidad) {
		return new ArtistaDTO(entidad.getIdArtista(), entidad.getNombre());
	}

	public static Artista toEntity(ArtistaDTO dto) {
		return new Artista(dto.idArtista(), dto.nombre());
	}

}
