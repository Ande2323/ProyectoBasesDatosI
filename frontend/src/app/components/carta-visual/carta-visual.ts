import { Component, computed, input } from '@angular/core';

import { CartaJuego } from '../../models/modelos';
import { simbolosDeMana } from '../../utils/formato';

interface Tema {
  marco: string;
  fondo: string;
  icono: string;
}

const RAYO = 'M34 4 12 38h14l-6 26 28-38H32z';
const TEMAS: Record<string, Tema> = {
  Blanco: { marco: '#e8dfc4', fondo: '#fdf7dd', icono: 'M32 6l6 18 19 1-15 11 6 18-16-11-16 11 6-18L7 25l19-1z' },
  Azul: { marco: '#2f73d9', fondo: '#bcd9ff', icono: 'M32 5C22 22 12 32 12 43a20 20 0 0 0 40 0C52 32 42 22 32 5z' },
  Negro: { marco: '#3a3540', fondo: '#a79fb0', icono: 'M32 6C18 6 10 17 10 28c0 9 5 14 10 17v9h24v-9c5-3 10-8 10-17C54 17 46 6 32 6zm-10 26a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm20 0a5 5 0 1 1 0-10 5 5 0 0 1 0 10z' },
  Rojo: { marco: '#d8402f', fondo: '#ffc2a8', icono: RAYO },
  Verde: { marco: '#2c9150', fondo: '#bfe8b5', icono: 'M52 8C28 8 12 20 12 40c0 5 1 9 3 12l-5 6 4 3 5-6c3 2 7 3 11 3 18 0 22-28 22-50z' },
};
const INCOLORA: Tema = { marco: '#9a9488', fondo: '#e7e2d3', icono: 'M32 6 54 19v26L32 58 10 45V19z' };
const MANA: Record<string, string> = { W: '#fdf7dd', U: '#7fb4f5', B: '#a79fb0', R: '#ff9d7c', G: '#8fd49a' };

/** Muestra la foto de la copia. Las copias que aún no tienen foto se dibujan con CSS según su color. */
@Component({
  selector: 'app-carta-visual',
  template: `
    @if (imagen(); as src) {
      <img class="foto" [src]="src" [alt]="carta().nombreOficial" />
    } @else {
    <article class="carta" [style.--marco]="tema().marco" [style.--fondo]="tema().fondo">
      <div class="barra">
        <strong class="nombre">{{ carta().nombreOficial }}</strong>
        <span class="mana">
          @for (s of simbolos(); track $index) {
            <i class="simbolo" [style.background]="colorSimbolo(s)">{{ s }}</i>
          }
        </span>
      </div>
      <div class="arte">
        <svg viewBox="0 0 64 64" aria-hidden="true"><path [attr.d]="tema().icono" /></svg>
      </div>
      <div class="barra barra--tipo">{{ lineaTipos() }}</div>
      <p class="texto">{{ carta().textoOraculo }}</p>
      @if (fuerza()) {
        <span class="fuerza">{{ fuerza() }}</span>
      }
    </article>
    }
  `,
  styleUrl: './carta-visual.css',
})
export class CartaVisual {
  readonly carta = input.required<CartaJuego>();
  readonly nombreColor = input<string | undefined>();
  readonly lineaTipos = input('');
  /** La foto de una copia; sin ella, la carta se dibuja. */
  readonly imagen = input<string | null>(null);

  protected readonly tema = computed(() => {
    const nombre = this.nombreColor();
    return (nombre && TEMAS[nombre]) || INCOLORA;
  });
  protected readonly simbolos = computed(() => simbolosDeMana(this.carta().costeMana));
  protected readonly fuerza = computed(() => {
    const c = this.carta();
    if (c.poder !== null && c.resistencia !== null) {
      return `${c.poder}/${c.resistencia}`;
    }
    return c.lealtad;
  });

  protected colorSimbolo(simbolo: string): string {
    return MANA[simbolo] ?? '#e7e2d3';
  }
}
