package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.CondicionDAO;
import co.edu.unbosque.dao.impl.CondicionDAOImpl;
import co.edu.unbosque.dto.CondicionDTO;
import co.edu.unbosque.mapper.CondicionMapper;
import co.edu.unbosque.model.Condicion;

public class CondicionController {

	private CondicionDAO condicionDAO;

	public CondicionController() {
		condicionDAO = new CondicionDAOImpl();
	}

	public boolean agregarCondicion(CondicionDTO dto) {
		if (!esValida(dto)) {
			return false;
		}
		return condicionDAO.crearCondicion(CondicionMapper.toEntity(dto));
	}

	public boolean actualizarCondicion(CondicionDTO dto, String codigoCondicion) {
		if (!esValida(dto)) {
			return false;
		}
		return condicionDAO.actualizarCondicion(CondicionMapper.toEntity(dto), codigoCondicion);
	}

	public boolean eliminarCondicion(String codigoCondicion) {
		return condicionDAO.eliminarCondicion(codigoCondicion);
	}

	public List<CondicionDTO> mostrarCondiciones() {
		return condicionDAO.mostrarCondiciones().stream().map(CondicionMapper::toDTO).toList();
	}

	public CondicionDTO buscarCondicion(String codigoCondicion) {
		Condicion condicion = condicionDAO.buscarCondicion(codigoCondicion);
		if (condicion == null) {
			return null;
		}
		return CondicionMapper.toDTO(condicion);
	}

	/** El factor de ajuste multiplica el precio de referencia: debe estar en (0, 1]. */
	private boolean esValida(CondicionDTO dto) {
		if (dto == null || dto.codigoCondicion() == null || dto.codigoCondicion().isBlank()
				|| dto.codigoCondicion().length() > 5) {
			return false;
		}
		if (dto.nombre() == null || dto.nombre().isBlank()) {
			return false;
		}
		return dto.factorAjuste() > 0 && dto.factorAjuste() <= 1;
	}

}
