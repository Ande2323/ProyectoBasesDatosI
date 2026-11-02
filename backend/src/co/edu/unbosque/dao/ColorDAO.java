package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Color;

public interface ColorDAO {

	public boolean crearColor(Color color);

	public boolean actualizarColor(Color color, int idColor);

	public boolean eliminarColor(int idColor);

	public List<Color> mostrarColores();

	public Color buscarColor(int idColor);

}
