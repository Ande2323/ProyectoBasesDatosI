package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Condicion;

public interface CondicionDAO {

	public boolean crearCondicion(Condicion c);

	public boolean actualizarCondicion(Condicion c, String codigoCondicion);

	public boolean eliminarCondicion(String codigoCondicion);

	public List<Condicion> mostrarCondiciones();

	public Condicion buscarCondicion(String codigoCondicion);

}
