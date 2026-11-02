import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Reserva } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';
import { fecha, precio } from '../../utils/formato';

@Component({
  selector: 'app-mis-reservas',
  imports: [RouterLink],
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.css',
})
export class MisReservas implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly exito = signal('');
  protected readonly cancelando = signal<number | null>(null);

  protected readonly fecha = fecha;
  protected readonly precio = precio;

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    this.api.reservas().subscribe({
      next: (reservas) => {
        this.reservas.set(reservas);
        this.cargando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.cargando.set(false);
      },
    });
  }

  protected claseEstado(estado: string): string {
    switch (estado) {
      case 'ACTIVA':
        return 'sello--activa';
      case 'CANCELADA':
        return 'sello--cancelada';
      case 'VENCIDA':
        return 'sello--vencida';
      default:
        return 'sello--cerrada';
    }
  }

  protected cancelar(reserva: Reserva): void {
    this.error.set('');
    this.exito.set('');
    this.cancelando.set(reserva.consecutivo);
    this.api.cancelarReserva(reserva.consecutivo).subscribe({
      next: () => {
        this.exito.set(`Reserva ${reserva.consecutivo} cancelada. Las copias quedaron libres.`);
        this.cancelando.set(null);
        this.cargar();
      },
      error: (e) => {
        this.error.set(mensajeDeError(e));
        this.cancelando.set(null);
      },
    });
  }
}
