package co.edu.unbosque.dao;

import java.util.List;

import co.edu.unbosque.model.Cliente;
import co.edu.unbosque.model.CuentaUsuario;

public interface ClienteDAO {

	/** Crea la cuenta y el cliente en una sola transacción. */
	public boolean registrarCliente(Cliente cliente, CuentaUsuario cuenta);

	public boolean actualizarCliente(Cliente cliente, String tipoDocumento, String numeroDocumento);

	public List<Cliente> mostrarClientes();

	public Cliente buscarCliente(String tipoDocumento, String numeroDocumento);

	public Cliente buscarClientePorCuenta(int idCuentaU);

}
