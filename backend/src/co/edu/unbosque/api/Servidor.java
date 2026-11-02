package co.edu.unbosque.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * Servidor HTTP del JDK (com.sun.net.httpserver). Aquí se registra cada ruta
 * de la API: un contexto por recurso, atendido por su Handler.
 */
public class Servidor {

	private HttpServer servidor;
	private int puerto;

	public Servidor(int puerto) throws IOException {
		this.puerto = puerto;
		this.servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
		registrarRutas();
		servidor.setExecutor(Executors.newFixedThreadPool(10));
	}

	private void registrarRutas() {
		servidor.createContext("/api/salud", new SaludHandler());
		// Cuentas e inicio de sesión
		servidor.createContext("/api/sesion", new SesionHandler());
		servidor.createContext("/api/clientes", new ClienteHandler());
		servidor.createContext("/api/empleados", new EmpleadoHandler());
		servidor.createContext("/api/cuentas", new CuentaUsuarioHandler());
		// Catálogos
		servidor.createContext("/api/colores", new ColorHandler());
		servidor.createContext("/api/tipos", new TipoHandler());
		servidor.createContext("/api/ediciones", new EdicionHandler());
		servidor.createContext("/api/idiomas", new IdiomaHandler());
		servidor.createContext("/api/rarezas", new RarezaHandler());
		servidor.createContext("/api/artistas", new ArtistaHandler());
		servidor.createContext("/api/condiciones", new CondicionHandler());
		servidor.createContext("/api/roles", new RolHandler());
		// Catálogo de cartas
		servidor.createContext("/api/cartas", new CartaHandler());
		servidor.createContext("/api/impresiones", new ImpresionHandler());
		// Inventario y operación
		servidor.createContext("/api/precios", new PrecioReferenciaHandler());
		servidor.createContext("/api/ejemplares", new EjemplarHandler());
		servidor.createContext("/api/reservas", new ReservaHandler());
		servidor.createContext("/api/salidas", new SalidaHandler());
		servidor.createContext("/api/estadisticas", new EstadisticaHandler());
	}

	public void iniciar() {
		servidor.start();
		System.out.println("Servidor iniciado en http://localhost:" + puerto + "/api");
	}

	public void detener() {
		servidor.stop(0);
	}
}
