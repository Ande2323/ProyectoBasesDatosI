package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.TipoDAO;
import co.edu.unbosque.dao.impl.TipoDAOImpl;
import co.edu.unbosque.dto.TipoDTO;
import co.edu.unbosque.mapper.TipoMapper;
import co.edu.unbosque.model.Tipo;

public class TipoController {

	private TipoDAO tipoDAO;

	public TipoController() {
		tipoDAO = new TipoDAOImpl();
	}

	/** Retorna el tipo creado con el id que asignó MySQL, o null si no se pudo. */
	public TipoDTO agregarTipo(TipoDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Tipo tipo = TipoMapper.toEntity(dto);
		if (!tipoDAO.crearTipo(tipo)) {
			return null;
		}
		return TipoMapper.toDTO(tipo);
	}

	public boolean actualizarTipo(TipoDTO dto, int idTipo) {
		if (!esValido(dto)) {
			return false;
		}
		return tipoDAO.actualizarTipo(TipoMapper.toEntity(dto), idTipo);
	}

	public boolean eliminarTipo(int idTipo) {
		return tipoDAO.eliminarTipo(idTipo);
	}

	public List<TipoDTO> mostrarTipos() {
		return tipoDAO.mostrarTipos().stream().map(TipoMapper::toDTO).toList();
	}

	public TipoDTO buscarTipo(int idTipo) {
		Tipo tipo = tipoDAO.buscarTipo(idTipo);
		if (tipo == null) {
			return null;
		}
		return TipoMapper.toDTO(tipo);
	}

	/** nivel debe ser 1 (supertipo), 2 (tipo) o 3 (subtipo). */
	private boolean esValido(TipoDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.nivel() < 1 || dto.nivel() > 3
				|| dto.codigo() == null || dto.codigo().isBlank() || dto.codigo().length() > 10
				|| dto.nombre() == null || dto.nombre().isBlank() || dto.nombre().length() > 50);
	}

}
