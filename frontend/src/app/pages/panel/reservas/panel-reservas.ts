import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Reserva } from '../../../models/modelos';
import { fecha } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

@Component({
  selector: 'app-panel-reservas',
  imports: [FormsModule],
  templateUrl: './panel-reservas.html',
})
export class PanelReservas extends PaginaPanel implements OnInit {
  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly cargando = signal(true);
  protected readonly estado = signal('ACTIVA');
  protected readonly estados = ['ACTIVA', 'ENTREGADA', 'VENCIDA', 'CANCELADA', ''];
  protected readonly entregando = signal<number | null>(null);
  protected seleccion: Record<number, boolean> = {};
  protected observaciones = '';

  protected readonly fecha = fecha;
  protected readonly vacia = computed(() => !this.cargando() && this.reservas().length === 0);

  ngOnInit(): void {
    this.cargar();
  }

  protected cambiarEstado(estado: string): void {
    this.estado.set(estado);
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.api.reservasPorEstado(this.estado()).subscribe({
      next: (r) => {
        this.reservas.set(r);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected claseEstado(estado: string): string {
    return { ACTIVA: 'sello--activa', VENCIDA: 'sello--vencida', CANCELADA: 'sello--cancelada' }[estado] ?? 'sello--cerrada';
  }

  protected abrirEntrega(r: Reserva): void {
    this.seleccion = Object.fromEntries(r.apartados.map((a) => [a.idEjemplar, true]));
    this.observaciones = '';
    this.entregando.set(r.consecutivo);
  }

  /** Lo que no se marque vuelve a estar disponible: una reserva origina una sola salida. */
  protected entregar(r: Reserva): void {
    const ejemplares = r.apartados.filter((a) => this.seleccion[a.idEjemplar]).map((a) => a.idEjemplar);
    this.ejecutar(
      this.api.registrarSalida({ consecutivoReserva: r.consecutivo, ejemplares, observaciones: this.observaciones || null }),
      `Reserva ${r.consecutivo} entregada.`,
      () => {
        this.entregando.set(null);
        this.cargar();
      },
    );
  }

  protected ningunoElegido(r: Reserva): boolean {
    return !r.apartados.some((a) => this.seleccion[a.idEjemplar]);
  }
}
