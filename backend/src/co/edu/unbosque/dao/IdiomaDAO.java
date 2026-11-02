package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Idioma;

public interface IdiomaDAO {

	public boolean crearIdioma(Idioma idioma);

	public boolean actualizarIdioma(Idioma idioma, int idIdioma);

	public List<Idioma> mostrarIdiomas();

	public Idioma buscarIdioma(int idIdioma);

}
