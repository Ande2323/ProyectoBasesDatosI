package co.edu.unbosque;

import java.io.IOException;

import co.edu.unbosque.api.Servidor;
import co.edu.unbosque.util.Configuracion;

public class Aplicacion {

	public static void main(String[] args) throws IOException {
		int puerto = Integer.parseInt(Configuracion.obtener("servidor.puerto", "8080"));
		Servidor servidor = new Servidor(puerto);
		Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener));
		servidor.iniciar();
	}

}
