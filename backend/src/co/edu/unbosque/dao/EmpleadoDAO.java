package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.model.Empleado;

public interface EmpleadoDAO {

	/** Crea la cuenta y el empleado en una sola transacción. */
	public boolean registrarEmpleado(Empleado empleado, CuentaUsuario cuenta);

	public boolean actualizarEmpleado(Empleado empleado, String tipoDocumento, String numeroDocumento);

	/** Baja lógica: el empleado pasa a RETIRADO y su cuenta a INACTIVA; nada se borra. */
	public boolean darDeBajaEmpleado(String tipoDocumento, String numeroDocumento);

	public List<Empleado> mostrarEmpleados();

	public Empleado buscarEmpleado(String tipoDocumento, String numeroDocumento);

	public Empleado buscarEmpleadoPorCuenta(int idCuentaU);

}
