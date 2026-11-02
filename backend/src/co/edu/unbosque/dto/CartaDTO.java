package co.edu.unbosque.dto;

import java.util.List;

/** valorMana es derivado: lo llena el mapper a partir de costeMana y se ignora al guardar. */
public record CartaDTO(int idCarta, String nombreOficial, String textoOraculo, String costeMana, String poder,
		String resistencia, String lealtad, List<Integer> colores, List<Integer> tipos, int valorMana) {

}
