import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';

import { Encabezado } from './components/encabezado/encabezado';
import { SesionService } from './services/sesion.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Encabezado],
  template: `
    @if (sesion.actual() && !enPanel()) {
      <app-encabezado />
    }
    @if (sesion.bloqueada()) {
      <p class="aviso aviso--error" role="alert">Su cuenta se encuentra bloqueada. Puede consultar la información, pero no realizar reservas ni modificaciones.</p>
    }
    <router-outlet />
  `,
})
export class App {
  protected readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map((e) => (e as NavigationEnd).urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );
  protected readonly enPanel = computed(() => this.url().startsWith('/panel'));
}
