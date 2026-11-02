package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Rol;

public interface RolDAO {

	public List<Rol> mostrarRoles();

	public Rol buscarRol(String codigoRol);

}
