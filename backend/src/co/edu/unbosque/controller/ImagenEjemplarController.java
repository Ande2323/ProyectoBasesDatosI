package co.edu.unbosque.controller;

import java.util.Set;

import co.edu.unbosque.dao.EjemplarDAO;
import co.edu.unbosque.dao.ImagenEjemplarDAO;
import co.edu.unbosque.dao.impl.EjemplarDAOImpl;
import co.edu.unbosque.dao.impl.ImagenEjemplarDAOImpl;

public class ImagenEjemplarController {

	public static final int TAMANO_MAXIMO = 5 * 1024 * 1024;

	private ImagenEjemplarDAO imagenEjemplarDAO;
	private EjemplarDAO ejemplarDAO;

	public ImagenEjemplarController() {
		imagenEjemplarDAO = new ImagenEjemplarDAOImpl();
		ejemplarDAO = new EjemplarDAOImpl();
	}

	/** Retorna null si se guardó, o el motivo del rechazo. */
	public String guardarImagen(int idEjemplar, byte[] contenido) {
		if (ejemplarDAO.buscarEjemplar(idEjemplar) == null) {
			return "No existe el ejemplar " + idEjemplar;
		}
		String motivo = validarImagen(contenido);
		if (motivo != null) {
			return motivo;
		}
		return imagenEjemplarDAO.guardarImagen(idEjemplar, contenido) ? null : "No se pudo guardar la imagen";
	}

	/** Retorna null si la imagen sirve, o el motivo del rechazo. Toda copia debe tener una. */
	public String validarImagen(byte[] contenido) {
		if (contenido == null || contenido.length == 0) {
			return "La imagen es obligatoria";
		}
		if (contenido.length > TAMANO_MAXIMO) {
			return "La imagen no puede pesar más de 5 MB";
		}
		return tipoDeContenido(contenido) == null ? "La imagen debe ser JPG, PNG o WEBP" : null;
	}

	public byte[] leerImagen(int idEjemplar) {
		return imagenEjemplarDAO.leerImagen(idEjemplar);
	}

	public boolean tieneImagen(int idEjemplar) {
		return imagenEjemplarDAO.existeImagen(idEjemplar);
	}

	public Set<Integer> idsConImagen() {
		return imagenEjemplarDAO.mostrarIdsConImagen();
	}

	/** Mira los primeros bytes del archivo: el nombre o la extensión que traiga no prueban nada. */
	public String tipoDeContenido(byte[] b) {
		if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
			return "image/jpeg";
		}
		if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G' && b[4] == 0x0D
				&& b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
			return "image/png";
		}
		if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E'
				&& b[10] == 'B' && b[11] == 'P') {
			return "image/webp";
		}
		return null;
	}

}
