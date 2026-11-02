package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Artista;

public interface ArtistaDAO {

	public boolean crearArtista(Artista artista);

	public boolean actualizarArtista(Artista artista, int idArtista);

	public boolean eliminarArtista(int idArtista);

	public List<Artista> mostrarArtistas();

	public Artista buscarArtista(int idArtista);

}
