export type CodigoRol = 'CLI' | 'EMP' | 'ADM';

export interface Sesion {
  token: string;
  idCuentaU: number;
  nombreUsuario: string;
  codigoRol: CodigoRol;
  tipoDocumento: string;
  numeroDocumento: string;
  expira: string;
  bloqueada: boolean;
}

export interface Color {
  idColor: number;
  nombreColor: string;
  ordenCanonico: number | null;
}

export interface Tipo {
  idTipo: number;
  nivel: number;
  codigo: string;
  nombre: string;
}

export interface Edicion {
  idEdicion: number;
  codigoImpreso: string;
  nombre: string;
  fechaLanzamiento: string | null;
  cantidadCartas: number | null;
  tipoEdicion: string | null;
}

export interface Idioma {
  idIdioma: number;
  codigo: string;
  nombre: string;
}

export interface Condicion {
  codigoCondicion: string;
  nombre: string;
  factorAjuste: number;
}

export interface CartaJuego {
  idCarta: number;
  nombreOficial: string;
  textoOraculo: string | null;
  costeMana: string | null;
  valorMana: number;
  poder: string | null;
  resistencia: string | null;
  lealtad: string | null;
  colores: number[];
  tipos: number[];
}

export interface Impresion {
  idCarta: number;
  numeroImpresion: number;
  numeroColeccion: string;
  foil: boolean;
  idEdicion: number;
  idIdioma: number;
  idRareza: number;
  idArtista: number;
  originalNumeroImpresion: number | null;
  primeraEdicion: boolean;
}

export interface Copia {
  idEjemplar: number;
  codigoEtiqueta: string;
  idCarta: number;
  numeroImpresion: number;
  codigoCondicion: string;
  precioSugerido: number | null;
  tieneImagen: boolean;
  estado?: string;
}

export interface Apartado {
  idEjemplar: number;
  codigoEtiqueta: string;
  idCarta: number;
  nombreCarta: string;
  codigoCondicion: string;
  precioSugerido: number | null;
}

export interface Reserva {
  consecutivo: number;
  fechaCreacion: string;
  fechaVencimiento: string | null;
  estado: string;
  clienteTipoDocumento: string;
  clienteNumeroDocumento: string;
  consecutivoSalida: number | null;
  ejemplares: number[];
  apartados: Apartado[];
}

export interface Cliente {
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  idCuentaU: number;
}

export interface RegistroCliente {
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  nombreUsuario: string;
  contrasena: string;
}

export interface Rareza {
  idRareza: number;
  codigo: string;
  nombre: string;
}

export interface Artista {
  idArtista: number;
  nombre: string;
}

export interface Ejemplar extends Copia {
  ubicacion: string | null;
  estado: string;
  fechaIngreso: string | null;
  observacionesDanos: string | null;
  consecutivoSalida: number | null;
}

export interface PrecioReferencia {
  idCarta: number;
  numeroImpresion: number;
  fecha: string;
  valor: number;
  fuenteCotizacion: string | null;
}

export interface Salida {
  consecutivoSalida: number;
  motivo: string;
  observaciones: string | null;
  fecha: string;
  clienteTipoDocumento: string;
  clienteNumeroDocumento: string;
  empleadoTipoDocumento: string;
  empleadoNumeroDocumento: string;
  consecutivoReserva: number | null;
  ejemplares: number[];
}

export interface Empleado {
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  cargo: string | null;
  estado: string;
  fechaVinculacion: string;
  idCuentaU: number;
}

export interface CuentaUsuario {
  idCuentaU: number;
  nombreUsuario: string;
  estado: string;
  codigoRol: CodigoRol;
  ultimoAcceso: string | null;
}

export type Fila = Record<string, unknown>;
