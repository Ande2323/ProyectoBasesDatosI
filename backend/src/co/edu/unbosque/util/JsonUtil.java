package co.edu.unbosque.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversión JSON escrita a mano (el proyecto no usa librerías externas).
 *
 * Escritura: Map, List, String, Number, Boolean y null. Lectura: devuelve
 * Map<String, Object>, List<Object>, String, Long (enteros), Double
 * (decimales), Boolean o null. Si el texto no es JSON válido, leer() retorna
 * null.
 */
public class JsonUtil {

	// ------------------------------------------------------------ escritura

	public static String aJson(Object valor) {
		StringBuilder sb = new StringBuilder();
		escribir(valor, sb);
		return sb.toString();
	}

	private static void escribir(Object valor, StringBuilder sb) {
		if (valor == null) {
			sb.append("null");
		} else if (valor instanceof String texto) {
			escribirTexto(texto, sb);
		} else if (valor instanceof BigDecimal decimal) {
			sb.append(decimal.toPlainString());
		} else if (valor instanceof Number || valor instanceof Boolean) {
			sb.append(valor);
		} else if (valor instanceof Map<?, ?> mapa) {
			sb.append('{');
			boolean primero = true;
			for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
				if (!primero) {
					sb.append(',');
				}
				escribirTexto(String.valueOf(entrada.getKey()), sb);
				sb.append(':');
				escribir(entrada.getValue(), sb);
				primero = false;
			}
			sb.append('}');
		} else if (valor instanceof List<?> lista) {
			sb.append('[');
			for (int i = 0; i < lista.size(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				escribir(lista.get(i), sb);
			}
			sb.append(']');
		} else {
			escribirTexto(valor.toString(), sb);
		}
	}

	private static void escribirTexto(String texto, StringBuilder sb) {
		sb.append('"');
		for (char c : texto.toCharArray()) {
			switch (c) {
			case '"' -> sb.append("\\\"");
			case '\\' -> sb.append("\\\\");
			case '\n' -> sb.append("\\n");
			case '\r' -> sb.append("\\r");
			case '\t' -> sb.append("\\t");
			default -> {
				if (c < 0x20) {
					sb.append(String.format("\\u%04x", (int) c));
				} else {
					sb.append(c);
				}
			}
			}
		}
		sb.append('"');
	}

	// -------------------------------------------------------------- lectura

	public static Object leer(String json) {
		if (json == null) {
			return null;
		}
		try {
			Lector lector = new Lector(json);
			Object valor = lector.leerValor();
			lector.saltarEspacios();
			if (!lector.terminado()) {
				return null;
			}
			return valor;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/** Lee un objeto JSON; retorna null si el texto no es un objeto válido. */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> leerObjeto(String json) {
		Object valor = leer(json);
		if (valor instanceof Map) {
			return (Map<String, Object>) valor;
		}
		return null;
	}

	private static class Lector {

		private final String texto;
		private int posicion;

		Lector(String texto) {
			this.texto = texto;
			this.posicion = 0;
		}

		boolean terminado() {
			return posicion >= texto.length();
		}

		void saltarEspacios() {
			while (!terminado() && Character.isWhitespace(texto.charAt(posicion))) {
				posicion++;
			}
		}

		char actual() {
			if (terminado()) {
				throw new IllegalArgumentException("JSON incompleto");
			}
			return texto.charAt(posicion);
		}

		void esperar(char c) {
			saltarEspacios();
			if (actual() != c) {
				throw new IllegalArgumentException("Se esperaba '" + c + "' en la posición " + posicion);
			}
			posicion++;
		}

		Object leerValor() {
			saltarEspacios();
			char c = actual();
			if (c == '{') {
				return leerMapa();
			}
			if (c == '[') {
				return leerLista();
			}
			if (c == '"') {
				return leerTexto();
			}
			if (texto.startsWith("true", posicion)) {
				posicion += 4;
				return Boolean.TRUE;
			}
			if (texto.startsWith("false", posicion)) {
				posicion += 5;
				return Boolean.FALSE;
			}
			if (texto.startsWith("null", posicion)) {
				posicion += 4;
				return null;
			}
			return leerNumero();
		}

		Map<String, Object> leerMapa() {
			Map<String, Object> mapa = new LinkedHashMap<>();
			esperar('{');
			saltarEspacios();
			if (actual() == '}') {
				posicion++;
				return mapa;
			}
			while (true) {
				saltarEspacios();
				String clave = leerTexto();
				esperar(':');
				mapa.put(clave, leerValor());
				saltarEspacios();
				if (actual() == ',') {
					posicion++;
				} else {
					esperar('}');
					return mapa;
				}
			}
		}

		List<Object> leerLista() {
			List<Object> lista = new ArrayList<>();
			esperar('[');
			saltarEspacios();
			if (actual() == ']') {
				posicion++;
				return lista;
			}
			while (true) {
				lista.add(leerValor());
				saltarEspacios();
				if (actual() == ',') {
					posicion++;
				} else {
					esperar(']');
					return lista;
				}
			}
		}

		String leerTexto() {
			esperar('"');
			StringBuilder sb = new StringBuilder();
			while (true) {
				char c = actual();
				posicion++;
				if (c == '"') {
					return sb.toString();
				}
				if (c != '\\') {
					sb.append(c);
					continue;
				}
				char escape = actual();
				posicion++;
				switch (escape) {
				case '"', '\\', '/' -> sb.append(escape);
				case 'n' -> sb.append('\n');
				case 'r' -> sb.append('\r');
				case 't' -> sb.append('\t');
				case 'b' -> sb.append('\b');
				case 'f' -> sb.append('\f');
				case 'u' -> {
					if (posicion + 4 > texto.length()) {
						throw new IllegalArgumentException("Escape unicode incompleto");
					}
					sb.append((char) Integer.parseInt(texto.substring(posicion, posicion + 4), 16));
					posicion += 4;
				}
				default -> throw new IllegalArgumentException("Escape inválido: \\" + escape);
				}
			}
		}

		Object leerNumero() {
			int inicio = posicion;
			while (!terminado() && "+-0123456789.eE".indexOf(texto.charAt(posicion)) >= 0) {
				posicion++;
			}
			String numero = texto.substring(inicio, posicion);
			if (numero.isEmpty()) {
				throw new IllegalArgumentException("Valor inesperado en la posición " + inicio);
			}
			if (numero.contains(".") || numero.contains("e") || numero.contains("E")) {
				return Double.parseDouble(numero);
			}
			return Long.parseLong(numero);
		}
	}
}
