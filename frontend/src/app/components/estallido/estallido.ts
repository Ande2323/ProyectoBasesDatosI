import { Component, input } from '@angular/core';

/** La explosión de cómic con texto: ¡ZAP!, ¡YA APARTADA! */
@Component({
  selector: 'app-estallido',
  template: `<div class="estrella" [style.background]="fondo()" [style.font-size]="tamano()"><span [style.color]="letra()">{{ texto() }}</span></div>`,
  styles: `
    :host { display: inline-block; filter: drop-shadow(3px 3px 0 var(--tinta)); }
    .estrella {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1.2em 1.5em;
      font-family: var(--letra-comic);
      letter-spacing: 0.03em;
      white-space: nowrap;
      clip-path: polygon(50% 0, 60% 18%, 78% 6%, 76% 26%, 100% 24%, 84% 42%, 100% 58%, 78% 62%, 84% 88%, 62% 74%, 52% 100%, 42% 76%, 20% 92%, 24% 66%, 0 62%, 16% 46%, 2% 24%, 26% 28%, 22% 6%, 42% 18%);
    }
    span { -webkit-text-stroke: 0.5px var(--tinta); }
  `,
})
export class Estallido {
  readonly texto = input.required<string>();
  readonly fondo = input('var(--rojo)');
  readonly letra = input('var(--amarillo)');
  readonly tamano = input('1.8rem');
}
