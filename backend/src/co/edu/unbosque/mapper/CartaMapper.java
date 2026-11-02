package co.edu.unbosque.mapper;

import co.edu.unbosque.dto.CartaDTO;
import co.edu.unbosque.model.Carta;
import co.edu.unbosque.util.ManaUtil;

public class CartaMapper {

	public static CartaDTO toDTO(Carta entidad) {
		return new CartaDTO(entidad.getIdCarta(), entidad.getNombreOficial(), entidad.getTextoOraculo(),
				entidad.getCosteMana(), entidad.getPoder(), entidad.getResistencia(), entidad.getLealtad(),
				entidad.getColores(), entidad.getTipos(), ManaUtil.calcularValorMana(entidad.getCosteMana()));
	}

	public static Carta toEntity(CartaDTO dto) {
		return new Carta(dto.idCarta(), dto.nombreOficial(), dto.textoOraculo(), dto.costeMana(), dto.poder(),
				dto.resistencia(), dto.lealtad(), dto.colores(), dto.tipos());
	}

}
