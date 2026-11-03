import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-encabezado',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <header class="franja">
      <a class="marca" routerLink="/catalogo">CARD MAGIC</a>
      <nav>
        <a class="enlace" routerLink="/catalogo" routerLinkActive="enlace--activo">Catálogo</a>
        @if (sesion.esCliente()) {
          <a class="enlace" routerLink="/mis-reservas" routerLinkActive="enlace--activo">Mis reservas</a>
        }
        @if (!sesion.esCliente()) {
          <a class="enlace" routerLink="/panel">Panel</a>
        }
        <a class="enlace" routerLink="/perfil" routerLinkActive="enlace--activo">Mi perfil</a>
        <button class="enlace" type="button" (click)="salir()">Salir</button>
      </nav>
    </header>
  `,
  styleUrl: './encabezado.css',
})
export class Encabezado {
  protected readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  protected salir(): void {
    const irAEntrar = () => {
      this.sesion.olvidar();
      this.router.navigate(['/entrar']);
    };
    this.sesion.salir().subscribe({ next: irAEntrar, error: irAEntrar });
  }
}
