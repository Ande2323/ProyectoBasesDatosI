package co.edu.unbosque.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import co.edu.unbosque.dao.ClienteDAO;
import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.dao.EmpleadoDAO;
import co.edu.unbosque.dao.impl.ClienteDAOImpl;
import co.edu.unbosque.dao.impl.CuentaUsuarioDAOImpl;
import co.edu.unbosque.dao.impl.EmpleadoDAOImpl;
import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.mapper.CuentaUsuarioMapper;
import co.edu.unbosque.model.Cliente;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.model.Empleado;
import co.edu.unbosque.util.GestorSesiones;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.HashUtil;
import co.edu.unbosque.util.ValidacionUtil;

public class SesionController {

	private CuentaUsuarioDAO cuentaUsuarioDAO;
	private ClienteDAO clienteDAO;
	private EmpleadoDAO empleadoDAO;

	public SesionController() {
		cuentaUsuarioDAO = new CuentaUsuarioDAOImpl();
		clienteDAO = new ClienteDAOImpl();
		empleadoDAO = new EmpleadoDAOImpl();
	}

	/**
	 * Retorna la sesión abierta, o null si el usuario no existe, la contraseña no
	 * coincide, la cuenta está inactiva o el empleado fue dado de baja. Siempre se
	 * responde igual hacia afuera para no revelar qué usuarios existen. Una cuenta
	 * bloqueada sí entra, pero su sesión solo puede consultar.
	 */
	public SesionActiva iniciarSesion(String nombreUsuario, String contrasena) {
		if (nombreUsuario == null || contrasena == null) {
			return null;
		}
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuentaPorUsuario(ValidacionUtil.normalizarUsuario(nombreUsuario));
		if (cuenta == null || !coincide(contrasena, cuenta.getContrasena())
				|| !(cuenta.estaActiva() || cuenta.estaBloqueada())) {
			return null;
		}
		String tipoDocumento;
		String numeroDocumento;
		if ("CLI".equals(cuenta.getCodigoRol())) {
			Cliente cliente = clienteDAO.buscarClientePorCuenta(cuenta.getIdCuentaU());
			if (cliente == null) {
				return null;
			}
			tipoDocumento = cliente.getTipoDocumento();
			numeroDocumento = cliente.getNumeroDocumento();
		} else {
			Empleado empleado = empleadoDAO.buscarEmpleadoPorCuenta(cuenta.getIdCuentaU());
			if (empleado == null || !empleado.estaActivo()) {
				return null;
			}
			tipoDocumento = empleado.getTipoDocumento();
			numeroDocumento = empleado.getNumeroDocumento();
		}
		cuentaUsuarioDAO.registrarAcceso(cuenta.getIdCuentaU());
		return GestorSesiones.abrirSesion(cuenta.getIdCuentaU(), cuenta.getNombreUsuario(), cuenta.getCodigoRol(),
				tipoDocumento, numeroDocumento, cuenta.estaBloqueada());
	}

	public void cerrarSesion(String token) {
		GestorSesiones.cerrarSesion(token);
	}

	public CuentaUsuarioDTO buscarCuentaDeSesion(SesionActiva sesion) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(sesion.idCuentaU());
		return cuenta == null ? null : CuentaUsuarioMapper.toDTO(cuenta);
	}

	/** Retorna null si se cambió, o el motivo del rechazo. Cierra las demás sesiones de la cuenta. */
	public String cambiarContrasena(SesionActiva sesion, String actual, String nueva) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(sesion.idCuentaU());
		if (cuenta == null || actual == null || !coincide(actual, cuenta.getContrasena())) {
			return "La contraseña actual no es correcta";
		}
		if (!ValidacionUtil.esContrasenaValida(nueva)) {
			return "La nueva contraseña debe tener entre " + ValidacionUtil.CONTRASENA_MINIMA + " y 100 caracteres";
		}
		if (nueva.equals(actual)) {
			return "La nueva contraseña debe ser distinta de la actual";
		}
		if (!cuentaUsuarioDAO.cambiarContrasena(cuenta.getIdCuentaU(), HashUtil.generarSHA256(nueva))) {
			return "No se pudo cambiar la contraseña";
		}
		GestorSesiones.cerrarSesionesDe(cuenta.getIdCuentaU());
		return null;
	}

	/** Compara hashes en tiempo constante, para no dar pistas por lo que tarda. */
	private boolean coincide(String contrasena, String hashGuardado) {
		if (hashGuardado == null) {
			return false;
		}
		byte[] calculado = HashUtil.generarSHA256(contrasena).getBytes(StandardCharsets.UTF_8);
		return MessageDigest.isEqual(calculado, hashGuardado.trim().getBytes(StandardCharsets.UTF_8));
	}

}
