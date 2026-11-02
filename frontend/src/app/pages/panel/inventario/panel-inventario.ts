import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CartaJuego, Condicion, Edicion, Ejemplar, Idioma, Impresion, Reserva } from '../../../models/modelos';
import { fecha, precio } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

const TAMANO_MAXIMO = 5 * 1024 * 1024;
const TIPOS_IMAGEN = ['image/jpeg', 'image/png', 'image/webp'];

@Component({
  selector: 'app-panel-inventario',
  imports: [FormsModule],
  templateUrl: './panel-inventario.html',
  styleUrl: './panel-inventario.css',
})
export class PanelInventario extends PaginaPanel implements OnInit {
  protected readonly ejemplares = signal<Ejemplar[]>([]);
  protected readonly cartas = signal<CartaJuego[]>([]);
  private readonly impresiones = signal<Impresion[]>([]);
  private readonly ediciones = signal<Edicion[]>([]);
  private readonly idiomas = signal<Idioma[]>([]);
  protected readonly condiciones = signal<Condicion[]>([]);
  protected readonly porVencer = signal<Reserva[]>([]);
  protected readonly valorTotal = signal<number | null>(null);
  protected readonly cargando = signal(true);

  protected readonly texto = signal('');
  protected readonly estado = signal('');
  protected readonly condicion = signal('');

  protected readonly formularioAbierto = signal(false);
  protected readonly vistaPrevia = signal<string | null>(null);
  protected nuevo = this.vacio();

  protected readonly editando = signal<number | null>(null);
  protected edicion = { codigoCondicion: '', ubicacion: '', observacionesDanos: '' };

  /** Cambia al reemplazar una imagen, para que el navegador no muestre la anterior. */
  protected readonly version = signal(Date.now());

  protected readonly precio = precio;
  protected readonly fecha = fecha;

  protected readonly visibles = computed(() => {
    const texto = this.texto().trim().toLowerCase();
    return this.ejemplares().filter(
      (e) =>
        (!this.estado() || e.estado === this.estado()) &&
        (!this.condicion() || e.codigoCondicion === this.condicion()) &&
        (!texto || `${e.codigoEtiqueta} ${this.nombreCarta(e.idCarta)}`.toLowerCase().includes(texto)),
    );
  });
  protected readonly cuenta = computed(() => {
    const total = (estado: string) => this.ejemplares().filter((e) => e.estado === estado).length;
    return { disponibles: total('DISPONIBLE'), reservados: total('RESERVADO'), vendidos: total('VENDIDO'), baja: total('BAJA') };
  });

  ngOnInit(): void {
    forkJoin({
      cartas: this.api.cartas(),
      impresiones: this.api.impresiones(),
      ediciones: this.api.ediciones(),
      idiomas: this.api.idiomas(),
      condiciones: this.api.condiciones(),
    }).subscribe({
      next: (r) => {
        this.cartas.set(r.cartas);
        this.impresiones.set(r.impresiones);
        this.ediciones.set(r.ediciones);
        this.idiomas.set(r.idiomas);
        this.condiciones.set(r.condiciones);
        this.cargarInventario();
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  private cargarInventario(): void {
    forkJoin({
      ejemplares: this.api.ejemplares(),
      activas: this.api.reservasPorEstado('ACTIVA'),
      valor: this.api.estadistica<{ valorTotal: number }>('valor-inventario'),
    }).subscribe({
      next: (r) => {
        this.ejemplares.set(r.ejemplares);
        this.valorTotal.set(r.valor.valorTotal);
        this.porVencer.set(
          [...r.activas].sort((a, b) => (a.fechaVencimiento ?? '').localeCompare(b.fechaVencimiento ?? '')).slice(0, 5),
        );
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected nombreCarta(idCarta: number): string {
    return this.cartas().find((c) => c.idCarta === idCarta)?.nombreOficial ?? `Carta ${idCarta}`;
  }

  protected edicionDe(e: Ejemplar): string {
    const impresion = this.impresiones().find((i) => i.idCarta === e.idCarta && i.numeroImpresion === e.numeroImpresion);
    return this.ediciones().find((ed) => ed.idEdicion === impresion?.idEdicion)?.codigoImpreso ?? '—';
  }

  protected impresionesDe(idCarta: number | null): Impresion[] {
    return this.impresiones().filter((i) => i.idCarta === idCarta);
  }

  protected etiquetaImpresion(i: Impresion): string {
    const edicion = this.ediciones().find((e) => e.idEdicion === i.idEdicion);
    const idioma = this.idiomas().find((id) => id.idIdioma === i.idIdioma);
    return [edicion?.nombre, `n.º ${i.numeroColeccion}`, idioma?.nombre, i.foil ? 'Foil' : null].filter(Boolean).join(' · ');
  }

  protected urlImagen(e: Ejemplar): string {
    return `/api/ejemplares/${e.idEjemplar}/imagen?v=${this.version()}`;
  }

  protected claseEstado(estado: string): string {
    return { DISPONIBLE: 'sello--activa', RESERVADO: 'sello--aviso', VENDIDO: 'sello--cerrada', BAJA: 'sello--cancelada' }[estado] ?? '';
  }

  protected alCambiarCarta(): void {
    this.nuevo.numeroImpresion = null;
  }

  protected abrirFormulario(): void {
    this.nuevo = this.vacio();
    this.vistaPrevia.set(null);
    this.formularioAbierto.update((v) => !v);
  }

  /** La imagen es obligatoria; aquí se revisa antes de enviarla para avisar de una vez. */
  protected elegirImagen(evento: Event): void {
    const archivo = this.archivoDe(evento);
    this.vistaPrevia.set(null);
    if (!archivo) {
      return;
    }
    const motivo = this.motivoRechazo(archivo);
    if (motivo) {
      this.error.set(motivo);
      return;
    }
    this.error.set('');
    const lector = new FileReader();
    lector.onload = () => this.vistaPrevia.set(lector.result as string);
    lector.readAsDataURL(archivo);
  }

  protected registrar(): void {
    this.ejecutar(
      this.api.registrarEjemplar({ ...this.nuevo, imagen: this.vistaPrevia() }),
      '¡Ejemplar registrado!',
      () => {
        this.formularioAbierto.set(false);
        this.version.set(Date.now());
        this.cargarInventario();
      },
    );
  }

  protected reemplazarImagen(e: Ejemplar, evento: Event): void {
    const archivo = this.archivoDe(evento);
    if (!archivo) {
      return;
    }
    const motivo = this.motivoRechazo(archivo);
    if (motivo) {
      this.error.set(motivo);
      return;
    }
    this.ejecutar(this.api.reemplazarImagen(e.idEjemplar, archivo), `Imagen de ${e.codigoEtiqueta} guardada.`, () => {
      this.version.set(Date.now());
      this.cargarInventario();
    });
  }

  protected editar(e: Ejemplar): void {
    this.edicion = {
      codigoCondicion: e.codigoCondicion,
      ubicacion: e.ubicacion ?? '',
      observacionesDanos: e.observacionesDanos ?? '',
    };
    this.editando.set(e.idEjemplar);
  }

  protected guardar(e: Ejemplar): void {
    this.ejecutar(this.api.actualizarEjemplar(e.idEjemplar, this.edicion), `${e.codigoEtiqueta} actualizado.`, () => {
      this.editando.set(null);
      this.cargarInventario();
    });
  }

  protected darDeBaja(e: Ejemplar): void {
    if (!confirm(`¿Dar de baja ${e.codigoEtiqueta} por daño o pérdida? No se puede deshacer.`)) {
      return;
    }
    this.ejecutar(this.api.darDeBajaEjemplar(e.idEjemplar, this.edicion.observacionesDanos || null), `${e.codigoEtiqueta} dado de baja.`, () => {
      this.editando.set(null);
      this.cargarInventario();
    });
  }

  private archivoDe(evento: Event): File | null {
    const entrada = evento.target as HTMLInputElement;
    return entrada.files?.[0] ?? null;
  }

  private motivoRechazo(archivo: File): string | null {
    if (!TIPOS_IMAGEN.includes(archivo.type)) {
      return 'La imagen debe ser JPG, PNG o WEBP.';
    }
    return archivo.size > TAMANO_MAXIMO ? 'La imagen no puede pesar más de 5 MB.' : null;
  }

  private vacio() {
    return {
      codigoEtiqueta: '',
      idCarta: null as number | null,
      numeroImpresion: null as number | null,
      codigoCondicion: 'NM',
      ubicacion: '',
      observacionesDanos: '',
    };
  }
}
