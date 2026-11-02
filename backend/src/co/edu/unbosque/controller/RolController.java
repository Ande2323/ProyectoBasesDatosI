package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.RolDAO;
import co.edu.unbosque.dao.impl.RolDAOImpl;
import co.edu.unbosque.dto.RolDTO;
import co.edu.unbosque.mapper.RolMapper;
import co.edu.unbosque.model.Rol;

public class RolController {

	private RolDAO rolDAO;

	public RolController() {
		rolDAO = new RolDAOImpl();
	}

	public List<RolDTO> mostrarRoles() {
		return rolDAO.mostrarRoles().stream().map(RolMapper::toDTO).toList();
	}

	public RolDTO buscarRol(String codigoRol) {
		Rol rol = rolDAO.buscarRol(codigoRol);
		if (rol == null) {
			return null;
		}
		return RolMapper.toDTO(rol);
	}

}
