import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { SesionService } from '../../../services/sesion.service';

@Component({
  selector: 'app-panel-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './panel-layout.html',
  styleUrl: './panel-layout.css',
})
export class PanelLayout {
  protected readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  protected readonly menu = computed(() => [
    { ruta: 'inventario', texto: 'Inventario' },
    { ruta: 'reservas', texto: 'Reservas' },
    { ruta: 'salidas', texto: 'Salidas' },
    { ruta: 'precios', texto: 'Precios' },
    { ruta: 'cartas', texto: 'Cartas' },
    { ruta: 'estadisticas', texto: 'Estadísticas' },
    ...(this.sesion.esAdmin()
      ? [
          { ruta: 'catalogos', texto: 'Catálogos' },
          { ruta: 'usuarios', texto: 'Usuarios' },
        ]
      : []),
  ]);

  protected rol(): string {
    return this.sesion.esAdmin() ? 'Administrador' : 'Empleado';
  }

  protected salir(): void {
    const irAEntrar = () => {
      this.sesion.olvidar();
      this.router.navigate(['/entrar']);
    };
    this.sesion.salir().subscribe({ next: irAEntrar, error: irAEntrar });
  }
}
