package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Tipo;

public interface TipoDAO {

	public boolean crearTipo(Tipo tipo);

	public boolean actualizarTipo(Tipo tipo, int idTipo);

	public boolean eliminarTipo(int idTipo);

	public List<Tipo> mostrarTipos();

	public Tipo buscarTipo(int idTipo);

}
