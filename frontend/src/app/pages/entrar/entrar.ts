import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Estallido } from '../../components/estallido/estallido';
import { mensajeDeError } from '../../services/api.service';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-entrar',
  imports: [FormsModule, RouterLink, Estallido],
  templateUrl: './entrar.html',
  styleUrl: './entrar.css',
})
export class Entrar {
  private readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  protected usuario = '';
  protected contrasena = '';
  protected readonly error = signal('');
  protected readonly enviando = signal(false);

  protected entrar(): void {
    this.error.set('');
    this.enviando.set(true);
    this.sesion.entrar(this.usuario, this.contrasena).subscribe({
      next: () => this.router.navigate([this.sesion.inicio()]),
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.enviando.set(false);
      },
    });
  }
}
