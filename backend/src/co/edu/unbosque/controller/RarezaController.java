package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.RarezaDAO;
import co.edu.unbosque.dao.impl.RarezaDAOImpl;
import co.edu.unbosque.dto.RarezaDTO;
import co.edu.unbosque.mapper.RarezaMapper;
import co.edu.unbosque.model.Rareza;

public class RarezaController {

	private RarezaDAO rarezaDAO;

	public RarezaController() {
		rarezaDAO = new RarezaDAOImpl();
	}

	/** Retorna la rareza creada con el id que asignó MySQL, o null si no se pudo. */
	public RarezaDTO agregarRareza(RarezaDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Rareza rareza = RarezaMapper.toEntity(dto);
		if (!rarezaDAO.crearRareza(rareza)) {
			return null;
		}
		return RarezaMapper.toDTO(rareza);
	}

	public boolean actualizarRareza(RarezaDTO dto, int idRareza) {
		if (!esValido(dto)) {
			return false;
		}
		return rarezaDAO.actualizarRareza(RarezaMapper.toEntity(dto), idRareza);
	}

	public List<RarezaDTO> mostrarRarezas() {
		return rarezaDAO.mostrarRarezas().stream().map(RarezaMapper::toDTO).toList();
	}

	public RarezaDTO buscarRareza(int idRareza) {
		Rareza rareza = rarezaDAO.buscarRareza(idRareza);
		if (rareza == null) {
			return null;
		}
		return RarezaMapper.toDTO(rareza);
	}

	/** Código y nombre obligatorios. */
	private boolean esValido(RarezaDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.codigo() == null || dto.codigo().isBlank() || dto.codigo().length() > 5
				|| dto.nombre() == null || dto.nombre().isBlank() || dto.nombre().length() > 30);
	}

}
