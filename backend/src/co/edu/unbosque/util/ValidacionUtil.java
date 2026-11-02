package co.edu.unbosque.util;

import java.util.List;

/** Validaciones de datos de personas y cuentas que comparten clientes y empleados. */
public class ValidacionUtil {

	public static final List<String> TIPOS_DOCUMENTO = List.of("CC", "CE", "TI", "PA");
	public static final int CONTRASENA_MINIMA = 8;

	public static boolean esTipoDocumentoValido(String tipo) {
		return tipo != null && TIPOS_DOCUMENTO.contains(tipo);
	}

	/** Solo letras y números (el pasaporte puede llevar letras), de 4 a 20. */
	public static boolean esNumeroDocumentoValido(String numero) {
		return numero != null && numero.matches("[A-Za-z0-9]{4,20}");
	}

	/** Minúsculas, números, punto y guion bajo, de 4 a 50. */
	public static boolean esNombreUsuarioValido(String usuario) {
		return usuario != null && usuario.matches("[a-z0-9._]{4,50}");
	}

	public static boolean esContrasenaValida(String contrasena) {
		return contrasena != null && contrasena.length() >= CONTRASENA_MINIMA && contrasena.length() <= 100;
	}

	public static boolean esTextoObligatorio(String texto, int maximo) {
		return texto != null && !texto.isBlank() && texto.length() <= maximo;
	}

	/** Recorta espacios; deja null si viene null. */
	public static String recortar(String texto) {
		return texto == null ? null : texto.trim();
	}

	/** Usuario y documento se guardan sin espacios; el usuario en minúscula. */
	public static String normalizarUsuario(String usuario) {
		return usuario == null ? null : usuario.trim().toLowerCase();
	}

	public static String normalizarDocumento(String texto) {
		return texto == null ? null : texto.trim().toUpperCase();
	}
}
