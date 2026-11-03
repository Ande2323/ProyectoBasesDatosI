package co.edu.unbosque.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tabla CUENTA_USUARIO (3FN). contrasena guarda el hash SHA-256, nunca la
 * contraseña en claro. estado: ACTIVA; BLOQUEADA, que entra pero solo puede
 * consultar; o INACTIVA, la baja definitiva.
 */
public class CuentaUsuario implements Serializable {

	private static final long serialVersionUID = 904127365L;

	private int idCuentaU;
	private String nombreUsuario;
	private String contrasena;
	private String estado;
	private LocalDateTime fechaUAcceso;
	private String codigoRol;

	public CuentaUsuario(int idCuentaU, String nombreUsuario, String contrasena, String estado,
			LocalDateTime fechaUAcceso, String codigoRol) {
		this.idCuentaU = idCuentaU;
		this.nombreUsuario = nombreUsuario;
		this.contrasena = contrasena;
		this.estado = estado;
		this.fechaUAcceso = fechaUAcceso;
		this.codigoRol = codigoRol;
	}

	public boolean estaActiva() {
		return "ACTIVA".equals(estado);
	}

	public boolean estaBloqueada() {
		return "BLOQUEADA".equals(estado);
	}

	public int getIdCuentaU() {
		return idCuentaU;
	}

	public void setIdCuentaU(int idCuentaU) {
		this.idCuentaU = idCuentaU;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaUAcceso() {
		return fechaUAcceso;
	}

	public void setFechaUAcceso(LocalDateTime fechaUAcceso) {
		this.fechaUAcceso = fechaUAcceso;
	}

	public String getCodigoRol() {
		return codigoRol;
	}

	public void setCodigoRol(String codigoRol) {
		this.codigoRol = codigoRol;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
