package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.dao.EmpleadoDAO;
import co.edu.unbosque.dao.impl.CuentaUsuarioDAOImpl;
import co.edu.unbosque.dao.impl.EmpleadoDAOImpl;
import co.edu.unbosque.dto.EmpleadoDTO;
import co.edu.unbosque.dto.RegistroEmpleadoDTO;
import co.edu.unbosque.mapper.EmpleadoMapper;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.model.Empleado;
import co.edu.unbosque.util.FechaUtil;
import co.edu.unbosque.util.GestorSesiones;
import co.edu.unbosque.util.GestorSesiones.SesionActiva;
import co.edu.unbosque.util.HashUtil;
import co.edu.unbosque.util.ValidacionUtil;

/** Solo el administrador registra empleados y los da de baja (sección G). */
public class EmpleadoController {

	private static final List<String> ROLES_EMPLEADO = List.of("EMP", "ADM");

	private EmpleadoDAO empleadoDAO;
	private CuentaUsuarioDAO cuentaUsuarioDAO;

	public EmpleadoController() {
		empleadoDAO = new EmpleadoDAOImpl();
		cuentaUsuarioDAO = new CuentaUsuarioDAOImpl();
	}

	/** Retorna el empleado con su cuenta, o null si no se pudo. */
	public EmpleadoDTO registrarEmpleado(RegistroEmpleadoDTO original) {
		if (validarRegistro(original) != null) {
			return null;
		}
		RegistroEmpleadoDTO dto = limpiar(original);
		Empleado empleado = new Empleado(dto.tipoDocumento(), dto.numeroDocumento(), dto.nombres(), dto.apellidos(),
				dto.cargo(), "ACTIVO", dto.fechaVinculacion(), 0);
		CuentaUsuario cuenta = new CuentaUsuario(0, dto.nombreUsuario(), HashUtil.generarSHA256(dto.contrasena()),
				"ACTIVA", null, dto.codigoRol());
		if (!empleadoDAO.registrarEmpleado(empleado, cuenta)) {
			return null;
		}
		return EmpleadoMapper.toDTO(empleado);
	}

	/** Cambia nombres, apellidos, cargo y fecha de vinculación. El estado se maneja con la baja. */
	public boolean actualizarEmpleado(EmpleadoDTO dto, String tipoDocumento, String numeroDocumento) {
		if (validarDatos(dto) != null) {
			return false;
		}
		Empleado empleado = EmpleadoMapper.toEntity(new EmpleadoDTO(tipoDocumento, numeroDocumento,
				dto.nombres().trim(), dto.apellidos().trim(), ValidacionUtil.recortar(dto.cargo()), null,
				dto.fechaVinculacion(), 0));
		return empleadoDAO.actualizarEmpleado(empleado, tipoDocumento, numeroDocumento);
	}

	/**
	 * Baja lógica del empleado y de su cuenta. Retorna null si se hizo, o el
	 * motivo del rechazo.
	 */
	public String darDeBajaEmpleado(String tipoDocumento, String numeroDocumento, SesionActiva quienLoHace) {
		Empleado empleado = empleadoDAO.buscarEmpleado(tipoDocumento, numeroDocumento);
		if (empleado == null) {
			return "No existe el empleado";
		}
		if (!empleado.estaActivo()) {
			return "El empleado ya estaba dado de baja";
		}
		if (empleado.getIdCuentaU() == quienLoHace.idCuentaU()) {
			return "No puede darse de baja a sí mismo";
		}
		CuentaUsuario cuenta = cuentaUsuarioDAO.buscarCuenta(empleado.getIdCuentaU());
		if (cuenta != null && "ADM".equals(cuenta.getCodigoRol()) && cuenta.estaActiva()
				&& cuentaUsuarioDAO.contarAdministradoresActivos() <= 1) {
			return "Es el último administrador activo: debe quedar al menos uno";
		}
		if (!empleadoDAO.darDeBajaEmpleado(tipoDocumento, numeroDocumento)) {
			return "No se pudo dar de baja al empleado";
		}
		GestorSesiones.cerrarSesionesDe(empleado.getIdCuentaU());
		return null;
	}

	public List<EmpleadoDTO> mostrarEmpleados() {
		return empleadoDAO.mostrarEmpleados().stream().map(EmpleadoMapper::toDTO).toList();
	}

	public EmpleadoDTO buscarEmpleado(String tipoDocumento, String numeroDocumento) {
		Empleado empleado = empleadoDAO.buscarEmpleado(tipoDocumento, numeroDocumento);
		return empleado == null ? null : EmpleadoMapper.toDTO(empleado);
	}

	/** Retorna null si los datos sirven, o el motivo del rechazo. */
	public String validarRegistro(RegistroEmpleadoDTO original) {
		if (original == null) {
			return "No llegaron los datos del empleado";
		}
		RegistroEmpleadoDTO dto = limpiar(original);
		if (!ValidacionUtil.esTipoDocumentoValido(dto.tipoDocumento())) {
			return "El tipo de documento debe ser CC, CE, TI o PA";
		}
		if (!ValidacionUtil.esNumeroDocumentoValido(dto.numeroDocumento())) {
			return "El número de documento debe tener entre 4 y 20 letras o números";
		}
		String motivo = validarDatos(new EmpleadoDTO(dto.tipoDocumento(), dto.numeroDocumento(), dto.nombres(),
				dto.apellidos(), dto.cargo(), null, dto.fechaVinculacion(), 0));
		if (motivo != null) {
			return motivo;
		}
		if (dto.codigoRol() == null || !ROLES_EMPLEADO.contains(dto.codigoRol())) {
			return "El rol de un empleado debe ser EMP o ADM";
		}
		if (!ValidacionUtil.esNombreUsuarioValido(dto.nombreUsuario())) {
			return "El usuario debe tener entre 4 y 50 caracteres: minúsculas, números, punto o guion bajo";
		}
		if (!ValidacionUtil.esContrasenaValida(dto.contrasena())) {
			return "La contraseña debe tener entre " + ValidacionUtil.CONTRASENA_MINIMA + " y 100 caracteres";
		}
		if (empleadoDAO.buscarEmpleado(dto.tipoDocumento(), dto.numeroDocumento()) != null) {
			return "Ya hay un empleado registrado con ese documento";
		}
		if (cuentaUsuarioDAO.buscarCuentaPorUsuario(dto.nombreUsuario()) != null) {
			return "Ese nombre de usuario ya está en uso";
		}
		return null;
	}

	/** Validaciones comunes al registrar y al actualizar. */
	public String validarDatos(EmpleadoDTO dto) {
		if (dto == null) {
			return "No llegaron los datos del empleado";
		}
		if (!ValidacionUtil.esTextoObligatorio(dto.nombres(), 100)
				|| !ValidacionUtil.esTextoObligatorio(dto.apellidos(), 100)) {
			return "Nombres y apellidos son obligatorios (máximo 100 caracteres cada uno)";
		}
		if (dto.cargo() != null && dto.cargo().length() > 50) {
			return "El cargo admite máximo 50 caracteres";
		}
		if (dto.fechaVinculacion() == null) {
			return "La fecha de vinculación es obligatoria (AAAA-MM-DD)";
		}
		if (dto.fechaVinculacion().isAfter(FechaUtil.hoy())) {
			return "La fecha de vinculación no puede ser futura";
		}
		return null;
	}

	private RegistroEmpleadoDTO limpiar(RegistroEmpleadoDTO dto) {
		String rol = dto.codigoRol() == null ? null : dto.codigoRol().trim().toUpperCase();
		return new RegistroEmpleadoDTO(ValidacionUtil.normalizarDocumento(dto.tipoDocumento()),
				ValidacionUtil.normalizarDocumento(dto.numeroDocumento()), ValidacionUtil.recortar(dto.nombres()),
				ValidacionUtil.recortar(dto.apellidos()), ValidacionUtil.recortar(dto.cargo()), dto.fechaVinculacion(),
				ValidacionUtil.normalizarUsuario(dto.nombreUsuario()), dto.contrasena(), rol);
	}

}
