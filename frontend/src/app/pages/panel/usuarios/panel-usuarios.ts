import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Cliente, CuentaUsuario, Empleado } from '../../../models/modelos';
import { fecha } from '../../../utils/formato';
import { PaginaPanel } from '../pagina-panel';

const ROLES: Record<string, string> = { CLI: 'Cliente', EMP: 'Empleado', ADM: 'Administrador' };

@Component({
  selector: 'app-panel-usuarios',
  imports: [FormsModule],
  templateUrl: './panel-usuarios.html',
})
export class PanelUsuarios extends PaginaPanel implements OnInit {
  protected readonly pestana = signal<'empleados' | 'clientes' | 'cuentas'>('empleados');
  protected readonly empleados = signal<Empleado[]>([]);
  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly cuentas = signal<CuentaUsuario[]>([]);
  protected readonly cargando = signal(true);
  protected readonly abierto = signal(false);
  protected nuevo = this.vacio();

  protected readonly fecha = fecha;

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    forkJoin({ empleados: this.api.empleados(), clientes: this.api.clientes(), cuentas: this.api.cuentas() }).subscribe({
      next: (r) => {
        this.empleados.set(r.empleados);
        this.clientes.set(r.clientes);
        this.cuentas.set(r.cuentas);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected nombreRol(codigo: string): string {
    return ROLES[codigo] ?? codigo;
  }

  protected usuarioDe(idCuentaU: number): string {
    return this.cuentas().find((c) => c.idCuentaU === idCuentaU)?.nombreUsuario ?? '';
  }

  protected esPropia(c: CuentaUsuario): boolean {
    return c.idCuentaU === this.sesion.actual()?.idCuentaU;
  }

  protected registrar(): void {
    this.ejecutar(this.api.registrarEmpleado(this.nuevo), '¡Empleado registrado!', () => {
      this.nuevo = this.vacio();
      this.abierto.set(false);
      this.cargar();
    });
  }

  protected darDeBaja(e: Empleado): void {
    if (!confirm(`¿Dar de baja a ${e.nombres} ${e.apellidos}? Su cuenta quedará inactiva y no se podrá reactivar.`)) {
      return;
    }
    this.ejecutar(this.api.darDeBajaEmpleado(e.tipoDocumento, e.numeroDocumento), 'Empleado dado de baja.', () => this.cargar());
  }

  protected cambiarEstado(c: CuentaUsuario, estado: string): void {
    this.ejecutar(this.api.cambiarEstadoCuenta(c.idCuentaU, estado), `Cuenta ${c.nombreUsuario}: ${estado}.`, () => this.cargar());
  }

  protected cambiarRol(c: CuentaUsuario, rol: string): void {
    this.ejecutar(this.api.cambiarRolCuenta(c.idCuentaU, rol), `Cuenta ${c.nombreUsuario}: ${this.nombreRol(rol)}.`, () => this.cargar());
  }

  private vacio() {
    return {
      tipoDocumento: 'CC',
      numeroDocumento: '',
      nombres: '',
      apellidos: '',
      cargo: '',
      fechaVinculacion: '',
      nombreUsuario: '',
      contrasena: '',
      codigoRol: 'EMP',
    };
  }
}
