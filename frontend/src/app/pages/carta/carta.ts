import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CartaVisual } from '../../components/carta-visual/carta-visual';
import { Estallido } from '../../components/estallido/estallido';
import { CartaJuego, Color, Condicion, Copia, Edicion, Idioma, Impresion, Tipo } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';
import { SesionService } from '../../services/sesion.service';
import { fecha, precio } from '../../utils/formato';

const SONIDOS: Record<string, string> = {
  Blanco: '¡FLASH!',
  Azul: '¡SPLASH!',
  Negro: '¡CRACK!',
  Rojo: '¡ZAP!',
  Verde: '¡CRUNCH!',
};

@Component({
  selector: 'app-carta',
  imports: [RouterLink, CartaVisual, Estallido],
  templateUrl: './carta.html',
  styleUrl: './carta.css',
})
export class Carta implements OnInit {
  private readonly api = inject(ApiService);
  private readonly ruta = inject(ActivatedRoute);
  protected readonly sesion = inject(SesionService);

  protected readonly carta = signal<CartaJuego | null>(null);
  protected readonly copias = signal<Copia[]>([]);
  private readonly impresiones = signal<Impresion[]>([]);
  private readonly ediciones = signal<Edicion[]>([]);
  private readonly idiomas = signal<Idioma[]>([]);
  private readonly colores = signal<Color[]>([]);
  private readonly tipos = signal<Tipo[]>([]);
  private readonly condiciones = signal<Condicion[]>([]);

  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly exito = signal('');
  protected readonly apartando = signal<number | null>(null);

  protected readonly precio = precio;

  /** La copia elegida en la lista; si no se ha elegido, la primera que tenga foto. */
  protected readonly copiaElegida = signal<number | null>(null);
  protected readonly imagen = computed(() => {
    const copias = this.copias();
    const elegida = copias.find((c) => c.idEjemplar === this.copiaElegida());
    const copia = elegida?.tieneImagen ? elegida : copias.find((c) => c.tieneImagen);
    return copia ? `/api/ejemplares/${copia.idEjemplar}/imagen` : null;
  });

  protected readonly nombreColor = computed(() => {
    const id = this.carta()?.colores[0];
    return this.colores().find((c) => c.idColor === id)?.nombreColor;
  });
  /** Según la palabra más larga, para que el nombre nunca se parta en mitad de una palabra. */
  protected readonly tamanoNombre = computed(() => {
    const larga = Math.max(...(this.carta()?.nombreOficial ?? '').split(' ').map((p) => p.length));
    if (larga <= 9) {
      return '4.6rem';
    }
    if (larga <= 12) {
      return '3.5rem';
    }
    return larga <= 16 ? '2.7rem' : '2rem';
  });
  protected readonly sonido = computed(() => SONIDOS[this.nombreColor() ?? ''] ?? '¡BOOM!');
  protected readonly lineaTipos = computed(() =>
    (this.carta()?.tipos ?? [])
      .map((id) => this.tipos().find((t) => t.idTipo === id))
      .filter((t) => !!t)
      .sort((a, b) => a.nivel - b.nivel)
      .map((t) => t.nombre)
      .join(' · '),
  );

  /** Una viñeta por edición en que salió la carta, de la más antigua a la más nueva. */
  protected readonly historia = computed(() => {
    const vistas = new Map<number, Edicion>();
    for (const impresion of this.impresiones()) {
      const edicion = this.ediciones().find((e) => e.idEdicion === impresion.idEdicion);
      if (edicion) {
        vistas.set(edicion.idEdicion, edicion);
      }
    }
    return [...vistas.values()]
      .sort((a, b) => (a.fechaLanzamiento ?? '').localeCompare(b.fechaLanzamiento ?? ''))
      .map((e) => ({ anio: e.fechaLanzamiento?.slice(0, 4) ?? '—', nombre: e.nombre }));
  });

  ngOnInit(): void {
    const idCarta = Number(this.ruta.snapshot.paramMap.get('id'));
    forkJoin({
      carta: this.api.carta(idCarta),
      copias: this.api.copiasDeCarta(idCarta),
      impresiones: this.api.impresionesDeCarta(idCarta),
      ediciones: this.api.ediciones(),
      idiomas: this.api.idiomas(),
      colores: this.api.colores(),
      tipos: this.api.tipos(),
      condiciones: this.api.condiciones(),
    }).subscribe({
      next: (r) => {
        this.carta.set(r.carta);
        this.copias.set(r.copias);
        this.impresiones.set(r.impresiones);
        this.ediciones.set(r.ediciones);
        this.idiomas.set(r.idiomas);
        this.colores.set(r.colores);
        this.tipos.set(r.tipos);
        this.condiciones.set(r.condiciones);
        this.cargando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.cargando.set(false);
      },
    });
  }

  protected condicion(copia: Copia): string {
    return this.condiciones().find((c) => c.codigoCondicion === copia.codigoCondicion)?.nombre ?? copia.codigoCondicion;
  }

  protected detalle(copia: Copia): string {
    const impresion = this.impresiones().find((i) => i.numeroImpresion === copia.numeroImpresion);
    if (!impresion) {
      return '';
    }
    const edicion = this.ediciones().find((e) => e.idEdicion === impresion.idEdicion);
    const idioma = this.idiomas().find((i) => i.idIdioma === impresion.idIdioma);
    return [edicion?.codigoImpreso, idioma?.nombre, impresion.foil ? 'Foil' : null].filter(Boolean).join(' · ');
  }

  protected apartar(copia: Copia): void {
    this.error.set('');
    this.exito.set('');
    this.apartando.set(copia.idEjemplar);
    this.api.apartar([copia.idEjemplar]).subscribe({
      next: (reserva) => {
        this.exito.set(`¡Apartada! Reserva ${reserva.consecutivo}: tienes hasta el ${fecha(reserva.fechaVencimiento)} para reclamarla.`);
        this.apartando.set(null);
        this.recargarCopias(copia.idCarta);
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.apartando.set(null);
        this.recargarCopias(copia.idCarta);
      },
    });
  }

  private recargarCopias(idCarta: number): void {
    this.api.copiasDeCarta(idCarta).subscribe((copias) => this.copias.set(copias));
  }
}
