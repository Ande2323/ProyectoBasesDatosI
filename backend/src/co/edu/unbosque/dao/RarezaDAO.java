package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Rareza;

public interface RarezaDAO {

	public boolean crearRareza(Rareza rareza);

	public boolean actualizarRareza(Rareza rareza, int idRareza);

	public boolean eliminarRareza(int idRareza);

	public List<Rareza> mostrarRarezas();

	public Rareza buscarRareza(int idRareza);

}
