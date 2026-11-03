import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { Sesion } from '../models/modelos';

const CLAVE = 'sesion-card-magic';

@Injectable({ providedIn: 'root' })
export class SesionService {
  private readonly http = inject(HttpClient);
  private readonly sesion = signal<Sesion | null>(this.leerGuardada());

  readonly actual = this.sesion.asReadonly();
  readonly esCliente = computed(() => this.sesion()?.codigoRol === 'CLI');
  readonly esAdmin = computed(() => this.sesion()?.codigoRol === 'ADM');
  readonly bloqueada = computed(() => this.sesion()?.bloqueada === true);
  /** A dónde va cada quien al entrar: el personal trabaja en el panel, el cliente en la tienda. */
  readonly inicio = computed(() => (this.esCliente() ? '/catalogo' : '/panel'));

  entrar(nombreUsuario: string, contrasena: string): Observable<Sesion> {
    return this.http.post<Sesion>('/api/sesion', { nombreUsuario, contrasena }).pipe(tap((s) => this.guardar(s)));
  }

  salir(): Observable<unknown> {
    return this.http.delete('/api/sesion').pipe(tap(() => this.olvidar()));
  }

  olvidar(): void {
    sessionStorage.removeItem(CLAVE);
    this.sesion.set(null);
  }

  private guardar(sesion: Sesion): void {
    sessionStorage.setItem(CLAVE, JSON.stringify(sesion));
    this.sesion.set(sesion);
  }

  private leerGuardada(): Sesion | null {
    const texto = sessionStorage.getItem(CLAVE);
    return texto ? (JSON.parse(texto) as Sesion) : null;
  }
}
