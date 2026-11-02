package co.edu.unbosque.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * valor_mana es un atributo derivado (3FN): no se guarda, se calcula a partir
 * de coste_mana.
 *
 * Reglas de Magic para el valor de maná:
 * - {3} vale 3; {X}, {Y}, {Z} valen 0.
 * - Cada símbolo de color o especial ({W}, {U}, {B}, {R}, {G}, {C}, {S}) vale 1.
 * - Híbridos: {W/U} vale 1; {2/W} vale 2 (cuenta la parte más alta).
 * - Pirexiano: {W/P} vale 1.
 */
public class ManaUtil {

	private static final Pattern SIMBOLO = Pattern.compile("\\{([^{}]+)\\}");
	private static final Pattern COSTE_VALIDO = Pattern.compile("^(\\{[0-9WUBRGCSXYZP/]+\\})*$");

	public static int calcularValorMana(String costeMana) {
		if (costeMana == null || costeMana.isBlank()) {
			return 0;
		}
		Matcher matcher = SIMBOLO.matcher(costeMana.toUpperCase());
		int total = 0;
		while (matcher.find()) {
			total += valorSimbolo(matcher.group(1));
		}
		return total;
	}

	/** Un coste vacío (tierras) es válido; si no, solo símbolos entre llaves. */
	public static boolean esCosteValido(String costeMana) {
		if (costeMana == null || costeMana.isBlank()) {
			return true;
		}
		return costeMana.length() <= 30 && COSTE_VALIDO.matcher(costeMana.toUpperCase()).matches();
	}

	private static int valorSimbolo(String simbolo) {
		if (simbolo.matches("\\d+")) {
			return Integer.parseInt(simbolo);
		}
		if (simbolo.equals("X") || simbolo.equals("Y") || simbolo.equals("Z")) {
			return 0;
		}
		if (simbolo.contains("/")) {
			int mayor = 1;
			for (String parte : simbolo.split("/")) {
				if (parte.matches("\\d+")) {
					mayor = Math.max(mayor, Integer.parseInt(parte));
				}
			}
			return mayor;
		}
		return 1;
	}
}
