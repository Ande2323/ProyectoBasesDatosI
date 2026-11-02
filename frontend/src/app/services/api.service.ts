import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Artista,
  CartaJuego,
  Cliente,
  Color,
  Condicion,
  Copia,
  CuentaUsuario,
  Edicion,
  Ejemplar,
  Empleado,
  Fila,
  Idioma,
  Impresion,
  PrecioReferencia,
  Rareza,
  RegistroCliente,
  Reserva,
  Salida,
  Tipo,
} from '../models/modelos';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  colores(): Observable<Color[]> {
    return this.http.get<Color[]>('/api/colores');
  }

  tipos(): Observable<Tipo[]> {
    return this.http.get<Tipo[]>('/api/tipos');
  }

  ediciones(): Observable<Edicion[]> {
    return this.http.get<Edicion[]>('/api/ediciones');
  }

  idiomas(): Observable<Idioma[]> {
    return this.http.get<Idioma[]>('/api/idiomas');
  }

  condiciones(): Observable<Condicion[]> {
    return this.http.get<Condicion[]>('/api/condiciones');
  }

  cartas(nombre = ''): Observable<CartaJuego[]> {
    return this.http.get<CartaJuego[]>('/api/cartas', { params: nombre ? { nombre } : {} });
  }

  carta(idCarta: number): Observable<CartaJuego> {
    return this.http.get<CartaJuego>(`/api/cartas/${idCarta}`);
  }

  impresionesDeCarta(idCarta: number): Observable<Impresion[]> {
    return this.http.get<Impresion[]>('/api/impresiones', { params: { idCarta } });
  }

  copiasDeCarta(idCarta: number): Observable<Copia[]> {
    return this.http.get<Copia[]>('/api/ejemplares/copias', { params: { idCarta } });
  }

  disponibles(): Observable<Copia[]> {
    return this.http.get<Copia[]>('/api/ejemplares/disponibles');
  }

  reservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>('/api/reservas');
  }

  apartar(ejemplares: number[]): Observable<Reserva> {
    return this.http.post<Reserva>('/api/reservas', { ejemplares });
  }

  cancelarReserva(consecutivo: number): Observable<Reserva> {
    return this.http.put<Reserva>(`/api/reservas/${consecutivo}/cancelar`, {});
  }

  registrarCliente(datos: RegistroCliente): Observable<Cliente> {
    return this.http.post<Cliente>('/api/clientes', datos);
  }

  cliente(tipo: string, numero: string): Observable<Cliente> {
    return this.http.get<Cliente>(`/api/clientes/${tipo}/${numero}`);
  }

  actualizarCliente(tipo: string, numero: string, nombres: string, apellidos: string): Observable<Cliente> {
    return this.http.put<Cliente>(`/api/clientes/${tipo}/${numero}`, { nombres, apellidos });
  }

  cambiarContrasena(actual: string, nueva: string): Observable<unknown> {
    return this.http.put('/api/sesion/contrasena', { actual, nueva });
  }

  // ---------------------------------------------------------------- panel

  rarezas(): Observable<Rareza[]> {
    return this.http.get<Rareza[]>('/api/rarezas');
  }

  artistas(): Observable<Artista[]> {
    return this.http.get<Artista[]>('/api/artistas');
  }

  impresiones(): Observable<Impresion[]> {
    return this.http.get<Impresion[]>('/api/impresiones');
  }

  ejemplares(): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>('/api/ejemplares');
  }

  registrarEjemplar(datos: Fila): Observable<Ejemplar> {
    return this.http.post<Ejemplar>('/api/ejemplares', datos);
  }

  actualizarEjemplar(id: number, datos: Fila): Observable<Ejemplar> {
    return this.http.put<Ejemplar>(`/api/ejemplares/${id}`, datos);
  }

  darDeBajaEjemplar(id: number, observacionesDanos: string | null): Observable<Ejemplar> {
    return this.http.put<Ejemplar>(`/api/ejemplares/${id}/baja`, { observacionesDanos });
  }

  reemplazarImagen(id: number, archivo: File): Observable<unknown> {
    return this.http.put(`/api/ejemplares/${id}/imagen`, archivo);
  }

  reservasPorEstado(estado: string): Observable<Reserva[]> {
    return this.http.get<Reserva[]>('/api/reservas', { params: estado ? { estado } : {} });
  }

  salidas(): Observable<Salida[]> {
    return this.http.get<Salida[]>('/api/salidas');
  }

  registrarSalida(datos: Fila): Observable<Salida> {
    return this.http.post<Salida>('/api/salidas', datos);
  }

  precios(idCarta: number, numeroImpresion: number): Observable<PrecioReferencia[]> {
    return this.http.get<PrecioReferencia[]>('/api/precios', { params: { idCarta, numeroImpresion } });
  }

  registrarPrecio(datos: Fila): Observable<PrecioReferencia> {
    return this.http.post<PrecioReferencia>('/api/precios', datos);
  }

  crearCarta(datos: Fila): Observable<CartaJuego> {
    return this.http.post<CartaJuego>('/api/cartas', datos);
  }

  actualizarCarta(id: number, datos: Fila): Observable<CartaJuego> {
    return this.http.put<CartaJuego>(`/api/cartas/${id}`, datos);
  }

  eliminarCarta(id: number): Observable<unknown> {
    return this.http.delete(`/api/cartas/${id}`);
  }

  crearImpresion(datos: Fila): Observable<Impresion> {
    return this.http.post<Impresion>('/api/impresiones', datos);
  }

  eliminarImpresion(idCarta: number, numero: number): Observable<unknown> {
    return this.http.delete(`/api/impresiones/${idCarta}/${numero}`);
  }

  listar(ruta: string): Observable<Fila[]> {
    return this.http.get<Fila[]>(`/api/${ruta}`);
  }

  crear(ruta: string, datos: Fila): Observable<Fila> {
    return this.http.post<Fila>(`/api/${ruta}`, datos);
  }

  actualizar(ruta: string, id: unknown, datos: Fila): Observable<unknown> {
    return this.http.put(`/api/${ruta}/${id}`, datos);
  }

  eliminar(ruta: string, id: unknown): Observable<unknown> {
    return this.http.delete(`/api/${ruta}/${id}`);
  }

  clientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>('/api/clientes');
  }

  empleados(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>('/api/empleados');
  }

  registrarEmpleado(datos: Fila): Observable<Empleado> {
    return this.http.post<Empleado>('/api/empleados', datos);
  }

  actualizarEmpleado(tipo: string, numero: string, datos: Fila): Observable<Empleado> {
    return this.http.put<Empleado>(`/api/empleados/${tipo}/${numero}`, datos);
  }

  darDeBajaEmpleado(tipo: string, numero: string): Observable<unknown> {
    return this.http.delete(`/api/empleados/${tipo}/${numero}`);
  }

  cuentas(): Observable<CuentaUsuario[]> {
    return this.http.get<CuentaUsuario[]>('/api/cuentas');
  }

  cambiarEstadoCuenta(id: number, estado: string): Observable<unknown> {
    return this.http.put(`/api/cuentas/${id}/estado`, { estado });
  }

  cambiarRolCuenta(id: number, codigoRol: string): Observable<unknown> {
    return this.http.put(`/api/cuentas/${id}/rol`, { codigoRol });
  }

  estadistica<T>(metrica: string): Observable<T> {
    return this.http.get<T>(`/api/estadisticas/${metrica}`);
  }
}

/** El backend explica cada rechazo en {"error": "..."}; aquí se saca ese texto. */
export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0 || error.status === 502 || error.status === 504) {
      return 'No hay conexión con el servidor. Revisa que el backend esté corriendo.';
    }
    if (typeof error.error?.error === 'string') {
      return error.error.error;
    }
  }
  return 'Algo salió mal. Intenta de nuevo.';
}
