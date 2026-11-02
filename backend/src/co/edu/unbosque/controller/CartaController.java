package co.edu.unbosque.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import co.edu.unbosque.dao.CartaDAO;
import co.edu.unbosque.dao.impl.CartaDAOImpl;
import co.edu.unbosque.dto.CartaDTO;
import co.edu.unbosque.mapper.CartaMapper;
import co.edu.unbosque.model.Carta;
import co.edu.unbosque.util.ManaUtil;

public class CartaController {

	private CartaDAO cartaDAO;

	public CartaController() {
		cartaDAO = new CartaDAOImpl();
	}

	/** Retorna la carta creada con el id que asignó MySQL, o null si no se pudo. */
	public CartaDTO agregarCarta(CartaDTO dto) {
		if (validarCarta(dto) != null) {
			return null;
		}
		Carta carta = CartaMapper.toEntity(limpiar(dto));
		if (!cartaDAO.crearCarta(carta)) {
			return null;
		}
		return CartaMapper.toDTO(carta);
	}

	public boolean actualizarCarta(CartaDTO dto, int idCarta) {
		if (validarCarta(dto) != null) {
			return false;
		}
		return cartaDAO.actualizarCarta(CartaMapper.toEntity(limpiar(dto)), idCarta);
	}

	public boolean eliminarCarta(int idCarta) {
		return cartaDAO.eliminarCarta(idCarta);
	}

	public List<CartaDTO> mostrarCartas() {
		return cartaDAO.mostrarCartas().stream().map(CartaMapper::toDTO).toList();
	}

	public List<CartaDTO> buscarCartasPorNombre(String texto) {
		if (texto == null || texto.isBlank()) {
			return mostrarCartas();
		}
		return cartaDAO.buscarCartasPorNombre(texto.trim()).stream().map(CartaMapper::toDTO).toList();
	}

	public CartaDTO buscarCarta(int idCarta) {
		Carta carta = cartaDAO.buscarCarta(idCarta);
		if (carta == null) {
			return null;
		}
		return CartaMapper.toDTO(carta);
	}

	/**
	 * Revisa los datos de la carta. Retorna null si todo está bien, o el motivo
	 * del rechazo para mostrárselo al usuario.
	 */
	public String validarCarta(CartaDTO original) {
		if (original == null) {
			return "No llegaron los datos de la carta";
		}
		CartaDTO dto = limpiar(original);
		if (dto.nombreOficial() == null || dto.nombreOficial().isBlank()) {
			return "El nombre oficial es obligatorio";
		}
		if (dto.nombreOficial().length() > 200) {
			return "El nombre oficial admite máximo 200 caracteres";
		}
		if (dto.textoOraculo() != null && dto.textoOraculo().length() > 2000) {
			return "El texto de oráculo admite máximo 2000 caracteres";
		}
		if (!ManaUtil.esCosteValido(dto.costeMana())) {
			return "El coste de maná debe escribirse con símbolos entre llaves, por ejemplo {2}{U}{U}";
		}
		if (excede(dto.poder(), 5) || excede(dto.resistencia(), 5) || excede(dto.lealtad(), 5)) {
			return "Poder, resistencia y lealtad admiten máximo 5 caracteres";
		}
		for (Integer color : dto.colores()) {
			if (color == null || color <= 0) {
				return "Cada color debe ser el id de un color existente";
			}
		}
		for (Integer tipo : dto.tipos()) {
			if (tipo == null || tipo <= 0) {
				return "Cada tipo debe ser el id de un tipo existente";
			}
		}
		if (dto.tipos().isEmpty()) {
			return "La carta debe tener al menos un tipo (por ejemplo Criatura o Instantáneo)";
		}
		return null;
	}

	private boolean excede(String texto, int maximo) {
		return texto != null && texto.length() > maximo;
	}

	/** Quita colores y tipos repetidos, y espacios del coste de maná. */
	private CartaDTO limpiar(CartaDTO dto) {
		List<Integer> colores = dto.colores() == null ? new ArrayList<>()
				: new ArrayList<>(new LinkedHashSet<>(dto.colores()));
		List<Integer> tipos = dto.tipos() == null ? new ArrayList<>()
				: new ArrayList<>(new LinkedHashSet<>(dto.tipos()));
		String nombre = dto.nombreOficial() == null ? null : dto.nombreOficial().trim();
		String coste = dto.costeMana() == null ? null : dto.costeMana().replace(" ", "").toUpperCase();
		return new CartaDTO(dto.idCarta(), nombre, dto.textoOraculo(), coste, dto.poder(), dto.resistencia(),
				dto.lealtad(), colores, tipos, 0);
	}

}
