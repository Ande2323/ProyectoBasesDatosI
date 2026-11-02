import { Component, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import { Fila } from '../../../models/modelos';
import { precio } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

interface Tablita {
  titulo: string;
  columnas: { clave: string; titulo: string }[];
  filas: Fila[];
}

/** Las métricas de la sección J: se calculan en el backend al consultarlas. */
@Component({
  selector: 'app-panel-estadisticas',
  templateUrl: './panel-estadisticas.html',
  styleUrl: './panel-estadisticas.css',
})
export class PanelEstadisticas extends PaginaPanel implements OnInit {
  protected readonly valor = signal<{ ejemplares: number; valorTotal: number } | null>(null);
  protected readonly tablas = signal<Tablita[]>([]);
  protected readonly cargando = signal(true);
  protected readonly precio = precio;

  ngOnInit(): void {
    forkJoin({
      valor: this.api.estadistica<{ ejemplares: number; valorTotal: number }>('valor-inventario'),
      estados: this.api.estadistica<Fila[]>('ejemplares-por-estado'),
      disponibilidad: this.api.estadistica<Fila[]>('disponibilidad'),
      rotacion: this.api.estadistica<Fila[]>('rotacion-edicion'),
      vencidas: this.api.estadistica<Fila[]>('reservas-vencidas'),
      salidas: this.api.estadistica<Fila[]>('salidas-empleado'),
      reediciones: this.api.estadistica<Fila[]>('reediciones'),
    }).subscribe({
      next: (r) => {
        this.valor.set(r.valor);
        this.tablas.set([
          { titulo: 'Ejemplares por estado', columnas: [{ clave: 'estado', titulo: 'Estado' }, { clave: 'cantidad', titulo: 'Cantidad' }], filas: r.estados },
          { titulo: 'Rotación por edición', columnas: [{ clave: 'nombre', titulo: 'Edición' }, { clave: 'ejemplares', titulo: 'Ejemplares' }, { clave: 'salidos', titulo: 'Salidos' }], filas: r.rotacion },
          { titulo: 'Salidas por empleado', columnas: [{ clave: 'nombres', titulo: 'Nombres' }, { clave: 'apellidos', titulo: 'Apellidos' }, { clave: 'salidas', titulo: 'Salidas' }], filas: r.salidas },
          { titulo: 'Reservas vencidas por cliente', columnas: [{ clave: 'nombres', titulo: 'Nombres' }, { clave: 'apellidos', titulo: 'Apellidos' }, { clave: 'reservasVencidas', titulo: 'Vencidas' }], filas: r.vencidas },
          { titulo: 'Reediciones por carta', columnas: [{ clave: 'nombreOficial', titulo: 'Carta' }, { clave: 'reediciones', titulo: 'Reediciones' }], filas: r.reediciones },
          { titulo: 'Disponibilidad por impresión', columnas: [{ clave: 'nombreOficial', titulo: 'Carta' }, { clave: 'numeroImpresion', titulo: 'Impresión' }, { clave: 'disponibles', titulo: 'Disponibles' }, { clave: 'reservados', titulo: 'Reservados' }, { clave: 'vendidos', titulo: 'Vendidos' }], filas: r.disponibilidad },
        ]);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }
}
