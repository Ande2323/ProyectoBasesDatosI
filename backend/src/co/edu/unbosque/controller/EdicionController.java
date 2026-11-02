package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.EdicionDAO;
import co.edu.unbosque.dao.impl.EdicionDAOImpl;
import co.edu.unbosque.dto.EdicionDTO;
import co.edu.unbosque.mapper.EdicionMapper;
import co.edu.unbosque.model.Edicion;

public class EdicionController {

	private EdicionDAO edicionDAO;

	public EdicionController() {
		edicionDAO = new EdicionDAOImpl();
	}

	/** Retorna la edición creada con el id que asignó MySQL, o null si no se pudo. */
	public EdicionDTO agregarEdicion(EdicionDTO dto) {
		if (!esValido(dto)) {
			return null;
		}
		Edicion edicion = EdicionMapper.toEntity(dto);
		if (!edicionDAO.crearEdicion(edicion)) {
			return null;
		}
		return EdicionMapper.toDTO(edicion);
	}

	public boolean actualizarEdicion(EdicionDTO dto, int idEdicion) {
		if (!esValido(dto)) {
			return false;
		}
		return edicionDAO.actualizarEdicion(EdicionMapper.toEntity(dto), idEdicion);
	}

	public boolean eliminarEdicion(int idEdicion) {
		return edicionDAO.eliminarEdicion(idEdicion);
	}

	public List<EdicionDTO> mostrarEdiciones() {
		return edicionDAO.mostrarEdiciones().stream().map(EdicionMapper::toDTO).toList();
	}

	public EdicionDTO buscarEdicion(int idEdicion) {
		Edicion edicion = edicionDAO.buscarEdicion(idEdicion);
		if (edicion == null) {
			return null;
		}
		return EdicionMapper.toDTO(edicion);
	}

	/** La cantidad de cartas, si se da, debe ser mayor que cero. */
	private boolean esValido(EdicionDTO dto) {
		if (dto == null) {
			return false;
		}
		return !(dto.codigoImpreso() == null || dto.codigoImpreso().isBlank() || dto.codigoImpreso().length() > 10
				|| dto.nombre() == null || dto.nombre().isBlank() || dto.nombre().length() > 100
				|| dto.cantidadCartas() != null && dto.cantidadCartas() <= 0
				|| dto.tipoEdicion() != null && dto.tipoEdicion().length() > 30);
	}

}
