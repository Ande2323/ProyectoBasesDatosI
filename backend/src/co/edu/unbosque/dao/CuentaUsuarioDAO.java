package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.CuentaUsuario;

/**
 * Las cuentas no se crean aquí sino junto con su titular (ClienteDAO y
 * EmpleadoDAO), porque una cuenta sin cliente ni empleado no puede existir.
 * Tampoco se borran: la baja es lógica (estado INACTIVA).
 */
public interface CuentaUsuarioDAO {

	public CuentaUsuario buscarCuenta(int idCuentaU);

	public CuentaUsuario buscarCuentaPorUsuario(String nombreUsuario);

	public List<CuentaUsuario> mostrarCuentas();

	public boolean cambiarEstadoCuenta(int idCuentaU, String estado);

	public boolean cambiarRolCuenta(int idCuentaU, String codigoRol);

	public boolean cambiarContrasena(int idCuentaU, String contrasenaCifrada);

	public void registrarAcceso(int idCuentaU);

	public int contarAdministradoresActivos();

}
