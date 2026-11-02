package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Edicion;

public interface EdicionDAO {

	public boolean crearEdicion(Edicion edicion);

	public boolean actualizarEdicion(Edicion edicion, int idEdicion);

	public boolean eliminarEdicion(int idEdicion);

	public List<Edicion> mostrarEdiciones();

	public Edicion buscarEdicion(int idEdicion);

}
