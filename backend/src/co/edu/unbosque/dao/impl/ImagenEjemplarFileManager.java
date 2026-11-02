package co.edu.unbosque.dao.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import co.edu.unbosque.dao.ImagenEjemplarDAO;
import co.edu.unbosque.util.Configuracion;

/**
 * Guarda cada imagen como imagenes/ejemplares/{idEjemplar}.{extension}. El nombre
 * sale siempre del id numérico, nunca del nombre del archivo que suben.
 */
public class ImagenEjemplarFileManager implements ImagenEjemplarDAO {

	private static final List<String> EXTENSIONES = List.of("jpg", "png", "webp");

	private Path carpeta() {
		return Path.of(Configuracion.obtener("imagenes.carpeta", "imagenes"), "ejemplares");
	}

	@Override
	public boolean guardarImagen(int idEjemplar, byte[] contenido, String extension) {
		try {
			Files.createDirectories(carpeta());
			Path temporal = carpeta().resolve(idEjemplar + ".subiendo");
			Files.write(temporal, contenido);
			borrarAnteriores(idEjemplar);
			Files.move(temporal, carpeta().resolve(idEjemplar + "." + extension), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] leerImagen(int idEjemplar) {
		String extension = buscarExtension(idEjemplar);
		if (extension == null) {
			return null;
		}
		try {
			return Files.readAllBytes(carpeta().resolve(idEjemplar + "." + extension));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String buscarExtension(int idEjemplar) {
		for (String extension : EXTENSIONES) {
			if (Files.exists(carpeta().resolve(idEjemplar + "." + extension))) {
				return extension;
			}
		}
		return null;
	}

	/** Una imagen nueva puede tener otra extensión: la anterior no debe quedar suelta. */
	private void borrarAnteriores(int idEjemplar) throws IOException {
		for (String extension : EXTENSIONES) {
			Files.deleteIfExists(carpeta().resolve(idEjemplar + "." + extension));
		}
	}

}
