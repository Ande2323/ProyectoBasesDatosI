package co.edu.unbosque.controller;

import java.util.List;

import co.edu.unbosque.dao.ClienteDAO;
import co.edu.unbosque.dao.CuentaUsuarioDAO;
import co.edu.unbosque.dao.impl.ClienteDAOImpl;
import co.edu.unbosque.dao.impl.CuentaUsuarioDAOImpl;
import co.edu.unbosque.dto.ClienteDTO;
import co.edu.unbosque.dto.RegistroClienteDTO;
import co.edu.unbosque.mapper.ClienteMapper;
import co.edu.unbosque.model.Cliente;
import co.edu.unbosque.model.CuentaUsuario;
import co.edu.unbosque.util.HashUtil;
import co.edu.unbosque.util.ValidacionUtil;

public class ClienteController {

	private ClienteDAO clienteDAO;
	private CuentaUsuarioDAO cuentaUsuarioDAO;

	public ClienteController() {
		clienteDAO = new ClienteDAOImpl();
		cuentaUsuarioDAO = new CuentaUsuarioDAOImpl();
	}

	/** Registro desde la página. Retorna el cliente con su cuenta, o null si no se pudo. */
	public ClienteDTO registrarCliente(RegistroClienteDTO original) {
		if (validarRegistro(original) != null) {
			return null;
		}
		RegistroClienteDTO dto = limpiar(original);
		Cliente cliente = new Cliente(dto.tipoDocumento(), dto.numeroDocumento(), dto.nombres(), dto.apellidos(), 0);
		CuentaUsuario cuenta = new CuentaUsuario(0, dto.nombreUsuario(), HashUtil.generarSHA256(dto.contrasena()),
				"ACTIVA", null, "CLI");
		if (!clienteDAO.registrarCliente(cliente, cuenta)) {
			return null;
		}
		return ClienteMapper.toDTO(cliente);
	}

	/** Solo se pueden cambiar nombres y apellidos: el documento es la llave. */
	public boolean actualizarCliente(ClienteDTO dto, String tipoDocumento, String numeroDocumento) {
		if (dto == null || !ValidacionUtil.esTextoObligatorio(dto.nombres(), 100)
				|| !ValidacionUtil.esTextoObligatorio(dto.apellidos(), 100)) {
			return false;
		}
		Cliente cliente = ClienteMapper.toEntity(new ClienteDTO(tipoDocumento, numeroDocumento,
				dto.nombres().trim(), dto.apellidos().trim(), 0));
		return clienteDAO.actualizarCliente(cliente, tipoDocumento, numeroDocumento);
	}

	public List<ClienteDTO> mostrarClientes() {
		return clienteDAO.mostrarClientes().stream().map(ClienteMapper::toDTO).toList();
	}

	public ClienteDTO buscarCliente(String tipoDocumento, String numeroDocumento) {
		Cliente cliente = clienteDAO.buscarCliente(tipoDocumento, numeroDocumento);
		return cliente == null ? null : ClienteMapper.toDTO(cliente);
	}

	/** Retorna null si los datos sirven, o el motivo del rechazo. */
	public String validarRegistro(RegistroClienteDTO original) {
		if (original == null) {
			return "No llegaron los datos del registro";
		}
		RegistroClienteDTO dto = limpiar(original);
		if (!ValidacionUtil.esTipoDocumentoValido(dto.tipoDocumento())) {
			return "El tipo de documento debe ser CC, CE, TI o PA";
		}
		if (!ValidacionUtil.esNumeroDocumentoValido(dto.numeroDocumento())) {
			return "El número de documento debe tener entre 4 y 20 letras o números";
		}
		if (!ValidacionUtil.esTextoObligatorio(dto.nombres(), 100)
				|| !ValidacionUtil.esTextoObligatorio(dto.apellidos(), 100)) {
			return "Nombres y apellidos son obligatorios (máximo 100 caracteres cada uno)";
		}
		if (!ValidacionUtil.esNombreUsuarioValido(dto.nombreUsuario())) {
			return "El usuario debe tener entre 4 y 50 caracteres: minúsculas, números, punto o guion bajo";
		}
		if (!ValidacionUtil.esContrasenaValida(dto.contrasena())) {
			return "La contraseña debe tener entre " + ValidacionUtil.CONTRASENA_MINIMA + " y 100 caracteres";
		}
		if (clienteDAO.buscarCliente(dto.tipoDocumento(), dto.numeroDocumento()) != null) {
			return "Ya hay un cliente registrado con ese documento";
		}
		if (cuentaUsuarioDAO.buscarCuentaPorUsuario(dto.nombreUsuario()) != null) {
			return "Ese nombre de usuario ya está en uso";
		}
		return null;
	}

	private RegistroClienteDTO limpiar(RegistroClienteDTO dto) {
		return new RegistroClienteDTO(ValidacionUtil.normalizarDocumento(dto.tipoDocumento()),
				ValidacionUtil.normalizarDocumento(dto.numeroDocumento()), ValidacionUtil.recortar(dto.nombres()),
				ValidacionUtil.recortar(dto.apellidos()), ValidacionUtil.normalizarUsuario(dto.nombreUsuario()),
				dto.contrasena());
	}

}
