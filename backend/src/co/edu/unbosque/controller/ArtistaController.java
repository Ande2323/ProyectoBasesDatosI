package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.ArtistaDAO;
import co.edu.unbosque.dao.impl.ArtistaDAOImpl;
import co.edu.unbosque.dto.ArtistaDTO;
import co.edu.unbosque.mapper.ArtistaMapper;
import co.edu.unbosque.model.Artista;

public class ArtistaController {

	private ArtistaDAO artistaDAO;

	public ArtistaController() {
		artistaDAO = new ArtistaDAOImpl();
	}

	/** Retorna el artista creado con el id que asignó MySQL, o null si no se pudo. */
	public ArtistaDTO agregarArtista(ArtistaDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Artista artista = ArtistaMapper.toEntity(dto);
		if (!artistaDAO.crearArtista(artista)) {
			return null;
		}
		return ArtistaMapper.toDTO(artista);
	}

	public boolean actualizarArtista(ArtistaDTO dto, int idArtista) {
		if (!esValido(dto)) {
			return false;
		}
		return artistaDAO.actualizarArtista(ArtistaMapper.toEntity(dto), idArtista);
	}

	public boolean eliminarArtista(int idArtista) {
		return artistaDAO.eliminarArtista(idArtista);
	}

	public List<ArtistaDTO> mostrarArtistas() {
		return artistaDAO.mostrarArtistas().stream().map(ArtistaMapper::toDTO).toList();
	}

	public ArtistaDTO buscarArtista(int idArtista) {
		Artista artista = artistaDAO.buscarArtista(idArtista);
		if (artista == null) {
			return null;
		}
		return ArtistaMapper.toDTO(artista);
	}

	/** El nombre es obligatorio. */
	private boolean esValido(ArtistaDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.nombre() == null || dto.nombre().isBlank() || dto.nombre().length() > 100);
	}

}
