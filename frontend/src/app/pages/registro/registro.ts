import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';

import { RegistroCliente } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  private readonly api = inject(ApiService);
  private readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  protected datos: RegistroCliente = {
    tipoDocumento: 'CC',
    numeroDocumento: '',
    nombres: '',
    apellidos: '',
    nombreUsuario: '',
    contrasena: '',
  };
  protected readonly error = signal('');
  protected readonly enviando = signal(false);

  protected registrar(): void {
    this.error.set('');
    this.enviando.set(true);
    this.api
      .registrarCliente(this.datos)
      .pipe(switchMap(() => this.sesion.entrar(this.datos.nombreUsuario, this.datos.contrasena)))
      .subscribe({
        next: () => this.router.navigate(['/catalogo']),
        error: (e) => {
          this.error.set(mensajeDeError(e));
          this.enviando.set(false);
        },
      });
  }
}
