package co.edu.unbosque.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Entrega conexiones JDBC a MySQL. Abrir una conexión cifrada hasta AWS tarda casi
 * un segundo, así que las que ya se usaron se guardan para reutilizarlas (un pool
 * sencillo, sin librerías). Los DAO no cambian: siguen abriéndola y cerrándola con
 * try-with-resources; close() la devuelve aquí en vez de cortarla. Requiere el
 * driver mysql-connector-j en lib/.
 */
public class ConexionBD {

	private static final int MAXIMO_LIBRES = 10;
	private static final long VALIDAR_TRAS_MILISEGUNDOS = 30_000;

	private record ConexionLibre(Connection real, long desde) {
	}

	private static final Deque<ConexionLibre> LIBRES = new ArrayDeque<>();

	public static Connection obtenerConexion() throws SQLException {
		Connection real = tomarLibre();
		if (real == null) {
			real = abrir();
		}
		return envolver(real);
	}

	public static boolean probarConexion() {
		try (Connection conexion = obtenerConexion()) {
			return conexion.isValid(3);
		} catch (SQLException e) {
			System.err.println("No fue posible conectar con la base de datos: " + e.getMessage());
			return false;
		}
	}

	private static Connection abrir() throws SQLException {
		String url = Configuracion.obtener("bd.url");
		String usuario = Configuracion.obtener("bd.usuario");
		String contrasena = Configuracion.obtener("bd.contrasena");
		if (url == null) {
			throw new SQLException("Falta bd.url en config/configuracion.properties");
		}
		return DriverManager.getConnection(url, usuario, contrasena);
	}

	/** La más reciente que siga viva. Si lleva rato sin usarse, MySQL pudo haberla cortado: se revisa. */
	private static Connection tomarLibre() {
		while (true) {
			ConexionLibre libre;
			synchronized (LIBRES) {
				libre = LIBRES.pollFirst();
			}
			if (libre == null) {
				return null;
			}
			boolean reciente = System.currentTimeMillis() - libre.desde() < VALIDAR_TRAS_MILISEGUNDOS;
			try {
				if (reciente || libre.real().isValid(2)) {
					return libre.real();
				}
			} catch (SQLException e) {
				// se descarta abajo
			}
			cerrar(libre.real());
		}
	}

	/** Deshace lo que haya quedado sin confirmar y la deja lista para el siguiente que la pida. */
	private static void devolver(Connection real) {
		try {
			if (!real.getAutoCommit()) {
				real.rollback();
				real.setAutoCommit(true);
			}
			synchronized (LIBRES) {
				if (LIBRES.size() < MAXIMO_LIBRES) {
					LIBRES.addFirst(new ConexionLibre(real, System.currentTimeMillis()));
					return;
				}
			}
		} catch (SQLException e) {
			// una conexión con problemas no se reutiliza
		}
		cerrar(real);
	}

	private static void cerrar(Connection real) {
		try {
			real.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Igual a la conexión real, excepto que close() la devuelve al pool. */
	private static Connection envolver(Connection real) {
		boolean[] cerrada = { false };
		return (Connection) Proxy.newProxyInstance(ConexionBD.class.getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, metodo, argumentos) -> {
					switch (metodo.getName()) {
					case "close":
						if (!cerrada[0]) {
							cerrada[0] = true;
							devolver(real);
						}
						return null;
					case "isClosed":
						return cerrada[0] || real.isClosed();
					default:
						if (cerrada[0]) {
							throw new SQLException("La conexión ya fue cerrada");
						}
						try {
							return metodo.invoke(real, argumentos);
						} catch (InvocationTargetException e) {
							throw e.getCause();
						}
					}
				});
	}
}
