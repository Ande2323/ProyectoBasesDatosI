import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CartaVisual } from '../../components/carta-visual/carta-visual';
import { CartaJuego, Color, Copia, Tipo } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';

@Component({
  selector: 'app-catalogo',
  imports: [FormsModule, RouterLink, CartaVisual],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly cartas = signal<CartaJuego[]>([]);
  protected readonly colores = signal<Color[]>([]);
  private readonly tipos = signal<Tipo[]>([]);
  private readonly copias = signal<Copia[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly colorActivo = signal<number | null>(null);
  protected readonly texto = signal('');

  protected readonly visibles = computed(() => {
    const color = this.colorActivo();
    const texto = this.texto().trim().toLowerCase();
    return this.cartas().filter(
      (c) =>
        this.disponibles(c) > 0 &&
        (color === null || c.colores.includes(color)) &&
        (!texto || c.nombreOficial.toLowerCase().includes(texto)),
    );
  });

  ngOnInit(): void {
    forkJoin({
      cartas: this.api.cartas(),
      colores: this.api.colores(),
      tipos: this.api.tipos(),
      copias: this.api.disponibles(),
    }).subscribe({
      next: (r) => {
        this.cartas.set(r.cartas);
        this.colores.set(r.colores);
        this.tipos.set(r.tipos);
        this.copias.set(r.copias);
        this.cargando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.cargando.set(false);
      },
    });
  }

  protected nombreColor(carta: CartaJuego): string | undefined {
    return this.colores().find((c) => c.idColor === carta.colores[0])?.nombreColor;
  }

  protected lineaTipos(carta: CartaJuego): string {
    return carta.tipos
      .map((id) => this.tipos().find((t) => t.idTipo === id))
      .filter((t) => !!t)
      .sort((a, b) => a.nivel - b.nivel)
      .map((t) => t.nombre)
      .join(' · ');
  }

  protected disponibles(carta: CartaJuego): number {
    return this.copias().filter((c) => c.idCarta === carta.idCarta).length;
  }

  /** La foto de la primera copia libre de la carta que tenga una. */
  protected imagen(carta: CartaJuego): string | null {
    const copia = this.copias().find((c) => c.idCarta === carta.idCarta && c.tieneImagen);
    return copia ? `/api/ejemplares/${copia.idEjemplar}/imagen` : null;
  }

  protected alternarColor(idColor: number): void {
    this.colorActivo.update((actual) => (actual === idColor ? null : idColor));
  }
}
