package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.dao.impl.CuentaUsuarioDAOImpl;
import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.mapper.CuentaUsuarioMapper;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.util.GestorSesiones;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;

/** Administración de usuarios: bloquear, desbloquear, dar de baja y asignar roles (solo ADM). */
public class CuentaUsuarioController {

	private static final String INACTIVA = "INACTIVA";
	private static final String DADA_DE_BAJA = "Esta cuenta fue dada de baja y no admite modificaciones";
	private static final List<String> ESTADOS = List.of("ACTIVA", INACTIVA, "BLOQUEADA");
	private static final List<String> ROLES_EMPLEADO = List.of("EMP", "ADM");

	private CuentaUsuarioDAO cuentaUsuarioDAO;

	public CuentaUsuarioController() {
		cuentaUsuarioDAO = new CuentaUsuarioDAOImpl();
	}

	public List<CuentaUsuarioDTO> mostrarCuentas() {
		return cuentaUsuarioDAO.mostrarCuentas().stream().map(CuentaUsuarioMapper::toDTO).toList();
	}

	public CuentaUsuarioDTO buscarCuenta(int idCuentaU) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(idCuentaU);
		return cuenta == null ? null : CuentaUsuarioMapper.toDTO(cuenta);
	}

	/**
	 * Bloquea, desbloquea o da de baja una cuenta. Retorna null si se hizo, o el
	 * motivo. Bloquear se puede deshacer; la baja no.
	 */
	public String cambiarEstado(int idCuentaU, String estado, SesionActiva quienLoHace) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(idCuentaU);
		if (cuenta == null) {
			return "No existe la cuenta " + idCuentaU;
		}
		if (INACTIVA.equals(cuenta.getEstado())) {
			return DADA_DE_BAJA;
		}
		String nuevo = estado == null ? null : estado.trim().toUpperCase();
		if (nuevo == null || !ESTADOS.contains(nuevo)) {
			return "El estado debe ser ACTIVA, INACTIVA o BLOQUEADA";
		}
		if (nuevo.equals(cuenta.getEstado())) {
			return "La cuenta ya está en estado " + nuevo;
		}
		if (idCuentaU == quienLoHace.idCuentaU()) {
			return "No puede cambiar el estado de su propia cuenta";
		}
		if (!"ACTIVA".equals(nuevo) && esUltimoAdmin(cuenta)) {
			return "Es el último administrador activo: debe quedar al menos uno";
		}
		if (!cuentaUsuarioDAO.cambiarEstadoCuenta(idCuentaU, nuevo)) {
			return "No se pudo cambiar el estado";
		}
		GestorSesiones.cerrarSesionesDe(idCuentaU);
		return null;
	}

	/** Asigna EMP o ADM a una cuenta de empleado. Retorna null si se hizo, o el motivo. */
	public String cambiarRol(int idCuentaU, String codigoRol, SesionActiva quienLoHace) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(idCuentaU);
		if (cuenta == null) {
			return "No existe la cuenta " + idCuentaU;
		}
		if (INACTIVA.equals(cuenta.getEstado())) {
			return DADA_DE_BAJA;
		}
		if (esCuentaDeCliente(cuenta)) {
			return "Las cuentas de cliente siempre tienen el rol CLI";
		}
		String nuevo = codigoRol == null ? null : codigoRol.trim().toUpperCase();
		if (nuevo == null || !ROLES_EMPLEADO.contains(nuevo)) {
			return "El rol de un empleado debe ser EMP o ADM";
		}
		if (nuevo.equals(cuenta.getCodigoRol())) {
			return "La cuenta ya tiene el rol " + nuevo;
		}
		if (idCuentaU == quienLoHace.idCuentaU()) {
			return "No puede cambiar su propio rol";
		}
		if ("EMP".equals(nuevo) && esUltimoAdmin(cuenta)) {
			return "Es el último administrador activo: debe quedar al menos uno";
		}
		if (!cuentaUsuarioDAO.cambiarRolCuenta(idCuentaU, nuevo)) {
			return "No se pudo cambiar el rol";
		}
		GestorSesiones.cerrarSesionesDe(idCuentaU);
		return null;
	}

	private boolean esCuentaDeCliente(CuentaUsuario cuenta) {
		return "CLI".equals(cuenta.getCodigoRol());
	}

	private boolean esUltimoAdmin(CuentaUsuario cuenta) {
		return "ADM".equals(cuenta.getCodigoRol()) && cuenta.estaActiva()
				&& cuentaUsuarioDAO.contarAdministradoresActivos() <= 1;
	}

}
