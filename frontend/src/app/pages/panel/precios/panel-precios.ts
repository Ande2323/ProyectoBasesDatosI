import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CartaJuego, Edicion, Idioma, Impresion, PrecioReferencia } from '../../../models/modelos';
import { fecha, precio } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

@Component({
  selector: 'app-panel-precios',
  imports: [FormsModule],
  templateUrl: './panel-precios.html',
})
export class PanelPrecios extends PaginaPanel implements OnInit {
  protected readonly cartas = signal<CartaJuego[]>([]);
  private readonly impresiones = signal<Impresion[]>([]);
  private readonly ediciones = signal<Edicion[]>([]);
  private readonly idiomas = signal<Idioma[]>([]);
  protected readonly historial = signal<PrecioReferencia[]>([]);

  protected idCarta: number | null = null;
  protected numero: number | null = null;
  protected valor: number | null = null;
  protected fuente = '';

  protected readonly precio = precio;
  protected readonly fecha = fecha;

  ngOnInit(): void {
    forkJoin({
      cartas: this.api.cartas(),
      impresiones: this.api.impresiones(),
      ediciones: this.api.ediciones(),
      idiomas: this.api.idiomas(),
    }).subscribe({
      next: (r) => {
        this.cartas.set(r.cartas);
        this.impresiones.set(r.impresiones);
        this.ediciones.set(r.ediciones);
        this.idiomas.set(r.idiomas);
      },
      error: (e) => this.mostrarError(e),
    });
  }

  protected impresionesDe(): Impresion[] {
    return this.impresiones().filter((i) => i.idCarta === this.idCarta);
  }

  protected etiqueta(i: Impresion): string {
    const edicion = this.ediciones().find((e) => e.idEdicion === i.idEdicion);
    const idioma = this.idiomas().find((id) => id.idIdioma === i.idIdioma);
    return [edicion?.nombre, idioma?.nombre, i.foil ? 'Foil' : null].filter(Boolean).join(' · ');
  }

  protected alCambiarCarta(): void {
    this.numero = null;
    this.historial.set([]);
  }

  protected consultar(): void {
    if (this.idCarta === null || this.numero === null) {
      this.historial.set([]);
      return;
    }
    this.api.precios(this.idCarta, this.numero).subscribe({
      next: (h) => this.historial.set(h),
      error: (e) => this.mostrarError(e),
    });
  }

  protected registrar(): void {
    this.ejecutar(
      this.api.registrarPrecio({
        idCarta: this.idCarta,
        numeroImpresion: this.numero,
        valor: this.valor,
        fuenteCotizacion: this.fuente || null,
      }),
      '¡Precio registrado!',
      () => {
        this.valor = null;
        this.fuente = '';
        this.consultar();
      },
    );
  }
}
