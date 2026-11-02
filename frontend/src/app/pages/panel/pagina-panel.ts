import { Directive, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService, mensajeDeError } from '../../services/api.service';
import { SesionService } from '../../services/sesion.service';

/** Lo que comparten las páginas del panel: avisos de error y de éxito, y ejecutar una acción. */
@Directive()
export abstract class PaginaPanel {
  protected readonly api = inject(ApiService);
  protected readonly sesion = inject(SesionService);

  protected readonly error = signal('');
  protected readonly exito = signal('');
  protected readonly ocupado = signal(false);

  protected ejecutar<T>(accion: Observable<T>, mensaje: string, luego?: (resultado: T) => void): void {
    this.error.set('');
    this.exito.set('');
    this.ocupado.set(true);
    accion.subscribe({
      next: (resultado) => {
        this.exito.set(mensaje);
        this.ocupado.set(false);
        luego?.(resultado);
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.ocupado.set(false);
      },
    });
  }

  protected mostrarError(e: unknown): void {
    this.error.set(mensajeDeError(e));
  }
}
