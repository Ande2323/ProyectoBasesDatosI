import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Artista, CartaJuego, Color, Edicion, Idioma, Impresion, Rareza, Tipo } from '../../../models/modelos';
import { PaginaPanel } from '../pagina-panel';

@Component({
  selector: 'app-panel-cartas',
  imports: [FormsModule],
  templateUrl: './panel-cartas.html',
})
export class PanelCartas extends PaginaPanel implements OnInit {
  protected readonly cartas = signal<CartaJuego[]>([]);
  protected readonly colores = signal<Color[]>([]);
  protected readonly tipos = signal<Tipo[]>([]);
  protected readonly impresiones = signal<Impresion[]>([]);
  protected readonly ediciones = signal<Edicion[]>([]);
  protected readonly idiomas = signal<Idioma[]>([]);
  protected readonly rarezas = signal<Rareza[]>([]);
  protected readonly artistas = signal<Artista[]>([]);
  protected readonly texto = signal('');
  protected readonly cargando = signal(true);

  protected readonly abierto = signal(false);
  protected readonly idEditando = signal<number | null>(null);
  protected forma = this.formaVacia();
  protected coloresElegidos: Record<number, boolean> = {};
  protected tiposElegidos: Record<number, boolean> = {};

  protected readonly cartaAbierta = signal<number | null>(null);
  protected impresion = this.impresionVacia();

  protected readonly visibles = computed(() => {
    const t = this.texto().trim().toLowerCase();
    return this.cartas().filter((c) => !t || c.nombreOficial.toLowerCase().includes(t));
  });
  protected readonly deCartaAbierta = computed(() => this.impresiones().filter((i) => i.idCarta === this.cartaAbierta()));
  /** Una reimpresión solo puede apuntar a una primera edición de la misma carta. */
  protected readonly originales = computed(() => this.deCartaAbierta().filter((i) => i.primeraEdicion));

  ngOnInit(): void {
    forkJoin({
      colores: this.api.colores(),
      tipos: this.api.tipos(),
      ediciones: this.api.ediciones(),
      idiomas: this.api.idiomas(),
      rarezas: this.api.rarezas(),
      artistas: this.api.artistas(),
    }).subscribe({
      next: (r) => {
        this.colores.set(r.colores);
        this.tipos.set(r.tipos);
        this.ediciones.set(r.ediciones);
        this.idiomas.set(r.idiomas);
        this.rarezas.set(r.rarezas);
        this.artistas.set(r.artistas);
        this.cargar();
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  private cargar(): void {
    forkJoin({ cartas: this.api.cartas(), impresiones: this.api.impresiones() }).subscribe({
      next: (r) => {
        this.cartas.set(r.cartas);
        this.impresiones.set(r.impresiones);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected nueva(): void {
    this.forma = this.formaVacia();
    this.coloresElegidos = {};
    this.tiposElegidos = {};
    this.idEditando.set(null);
    this.abierto.update((v) => !v);
  }

  protected editar(c: CartaJuego): void {
    this.forma = {
      nombreOficial: c.nombreOficial,
      textoOraculo: c.textoOraculo ?? '',
      costeMana: c.costeMana ?? '',
      poder: c.poder ?? '',
      resistencia: c.resistencia ?? '',
      lealtad: c.lealtad ?? '',
    };
    this.coloresElegidos = Object.fromEntries(c.colores.map((id) => [id, true]));
    this.tiposElegidos = Object.fromEntries(c.tipos.map((id) => [id, true]));
    this.idEditando.set(c.idCarta);
    this.abierto.set(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  protected guardar(): void {
    const datos = {
      ...this.forma,
      colores: this.elegidos(this.coloresElegidos),
      tipos: this.elegidos(this.tiposElegidos),
    };
    const id = this.idEditando();
    const accion = id === null ? this.api.crearCarta(datos) : this.api.actualizarCarta(id, datos);
    this.ejecutar<CartaJuego>(accion, id === null ? '¡Carta creada!' : 'Carta actualizada.', () => {
      this.abierto.set(false);
      this.cargar();
    });
  }

  protected eliminar(c: CartaJuego): void {
    if (!confirm(`¿Eliminar "${c.nombreOficial}"? Solo se puede si no tiene impresiones.`)) {
      return;
    }
    this.ejecutar(this.api.eliminarCarta(c.idCarta), 'Carta eliminada.', () => this.cargar());
  }

  protected alternarImpresiones(c: CartaJuego): void {
    this.impresion = this.impresionVacia();
    this.cartaAbierta.update((actual) => (actual === c.idCarta ? null : c.idCarta));
  }

  protected crearImpresion(idCarta: number): void {
    this.ejecutar(this.api.crearImpresion({ idCarta, ...this.impresion }), '¡Impresión creada!', () => {
      this.impresion = this.impresionVacia();
      this.cargar();
    });
  }

  protected eliminarImpresion(i: Impresion): void {
    if (!confirm('¿Eliminar esta impresión? Solo se puede si no tiene ejemplares, precios ni reimpresiones.')) {
      return;
    }
    this.ejecutar(this.api.eliminarImpresion(i.idCarta, i.numeroImpresion), 'Impresión eliminada.', () => this.cargar());
  }

  protected nombreEdicion(id: number): string {
    return this.ediciones().find((e) => e.idEdicion === id)?.nombre ?? '';
  }

  protected nombreIdioma(id: number): string {
    return this.idiomas().find((i) => i.idIdioma === id)?.nombre ?? '';
  }

  protected nombreRareza(id: number): string {
    return this.rarezas().find((r) => r.idRareza === id)?.nombre ?? '';
  }

  protected nombreArtista(id: number): string {
    return this.artistas().find((a) => a.idArtista === id)?.nombre ?? '';
  }

  private elegidos(mapa: Record<number, boolean>): number[] {
    return Object.keys(mapa).filter((k) => mapa[+k]).map(Number);
  }

  private formaVacia() {
    return { nombreOficial: '', textoOraculo: '', costeMana: '', poder: '', resistencia: '', lealtad: '' };
  }

  private impresionVacia() {
    return {
      numeroColeccion: '',
      foil: false,
      idEdicion: null as number | null,
      idIdioma: null as number | null,
      idRareza: null as number | null,
      idArtista: null as number | null,
      originalNumeroImpresion: null as number | null,
    };
  }
}
