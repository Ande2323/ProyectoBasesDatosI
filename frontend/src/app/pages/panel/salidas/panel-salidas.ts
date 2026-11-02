import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CartaJuego, Cliente, Ejemplar, Salida } from '../../../models/modelos';
import { fecha, precio } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

@Component({
  selector: 'app-panel-salidas',
  imports: [FormsModule],
  templateUrl: './panel-salidas.html',
})
export class PanelSalidas extends PaginaPanel implements OnInit {
  protected readonly salidas = signal<Salida[]>([]);
  protected readonly clientes = signal<Cliente[]>([]);
  private readonly ejemplares = signal<Ejemplar[]>([]);
  private readonly cartas = signal<CartaJuego[]>([]);
  protected readonly cargando = signal(true);
  protected readonly abierto = signal(false);

  protected cliente: Cliente | null = null;
  protected observaciones = '';
  protected seleccion: Record<number, boolean> = {};

  protected readonly fecha = fecha;
  protected readonly precio = precio;
  protected readonly disponibles = computed(() => this.ejemplares().filter((e) => e.estado === 'DISPONIBLE'));

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    forkJoin({
      salidas: this.api.salidas(),
      clientes: this.api.clientes(),
      ejemplares: this.api.ejemplares(),
      cartas: this.api.cartas(),
    }).subscribe({
      next: (r) => {
        this.salidas.set(r.salidas);
        this.clientes.set(r.clientes);
        this.ejemplares.set(r.ejemplares);
        this.cartas.set(r.cartas);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected descripcion(idEjemplar: number): string {
    const e = this.ejemplares().find((x) => x.idEjemplar === idEjemplar);
    if (!e) {
      return `Ejemplar ${idEjemplar}`;
    }
    return `${e.codigoEtiqueta} · ${this.cartas().find((c) => c.idCarta === e.idCarta)?.nombreOficial ?? ''}`;
  }

  protected abrir(): void {
    this.cliente = null;
    this.observaciones = '';
    this.seleccion = {};
    this.abierto.update((v) => !v);
  }

  protected elegidos(): number[] {
    return Object.keys(this.seleccion).filter((k) => this.seleccion[+k]).map(Number);
  }

  protected registrar(): void {
    if (!this.cliente) {
      return;
    }
    this.ejecutar(
      this.api.registrarSalida({
        clienteTipoDocumento: this.cliente.tipoDocumento,
        clienteNumeroDocumento: this.cliente.numeroDocumento,
        ejemplares: this.elegidos(),
        observaciones: this.observaciones || null,
      }),
      '¡Entrega registrada!',
      () => {
        this.abierto.set(false);
        this.cargar();
      },
    );
  }
}
