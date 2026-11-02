package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.dao.EmpleadoDAO;
import co.edu.unbosque.dao.impl.CuentaUsuarioDAOImpl;
import co.edu.unbosque.dao.impl.EmpleadoDAOImpl;
import co.edu.unbosque.dto.CuentaUsuarioDTO;
import co.edu.unbosque.mapper.CuentaUsuarioMapper;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.model.Empleado;
import co.edu.unbosque.util.GestorSesiones;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;

/** Administración de usuarios: dar de baja, reactivar y asignar roles (solo ADM). */
public class CuentaUsuarioController {

	private static final List<String> ESTADOS = List.of("ACTIVA", "INACTIVA", "BLOQUEADA");
	private static final List<String> ROLES_EMPLEADO = List.of("EMP", "ADM");

	private CuentaUsuarioDAO cuentaUsuarioDAO;
	private EmpleadoDAO empleadoDAO;

	public CuentaUsuarioController() {
		cuentaUsuarioDAO = new CuentaUsuarioDAOImpl();
		empleadoDAO = new EmpleadoDAOImpl();
	}

	public List<CuentaUsuarioDTO> mostrarCuentas() {
		return cuentaUsuarioDAO.mostrarCuentas().stream().map(CuentaUsuarioMapper::toDTO).toList();
	}

	public CuentaUsuarioDTO buscarCuenta(int idCuentaU) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(idCuentaU);
		return cuenta == null ? null : CuentaUsuarioMapper.toDTO(cuenta);
	}

	/** Da de baja, bloquea o reactiva una cuenta. Retorna null si se hizo, o el motivo. */
	public String cambiarEstado(int idCuentaU, String estado, SesionActiva quienLoHace) {
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(idCuentaU);
		if (cuenta == null) {
			return "No existe la cuenta " + idCuentaU;
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
		if ("ACTIVA".equals(nuevo) && !esCuentaDeCliente(cuenta)) {
			Empleado empleado = empleadoDAO.buscarEmpleadoPorCuenta(idCuentaU);
			if (empleado != null && !empleado.estaActivo()) {
				return "El empleado de esta cuenta fue dado de baja: su cuenta no puede reactivarse";
			}
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
