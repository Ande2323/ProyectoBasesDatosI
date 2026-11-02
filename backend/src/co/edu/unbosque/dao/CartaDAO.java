package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Carta;

public interface CartaDAO {

	public boolean crearCarta(Carta c);

	public boolean actualizarCarta(Carta c, int idCarta);

	public boolean eliminarCarta(int idCarta);

	public List<Carta> mostrarCartas();

	public List<Carta> buscarCartasPorNombre(String texto);

	public Carta buscarCarta(int idCarta);

}
