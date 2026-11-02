import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ApiService, mensajeDeError } from '../../services/api.service';
import { SesionService } from '../../services/sesion.service';

const ROLES: Record<string, string> = { CLI: 'Cliente', EMP: 'Empleado', ADM: 'Administrador' };

@Component({
  selector: 'app-perfil',
  imports: [FormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  protected readonly sesion = inject(SesionService);

  protected nombres = '';
  protected apellidos = '';
  protected actual = '';
  protected nueva = '';
  protected readonly errorDatos = signal('');
  protected readonly exitoDatos = signal('');
  protected readonly errorClave = signal('');

  protected rol(): string {
    return ROLES[this.sesion.actual()?.codigoRol ?? ''] ?? '';
  }

  ngOnInit(): void {
    const s = this.sesion.actual();
    if (s && this.sesion.esCliente()) {
      this.api.cliente(s.tipoDocumento, s.numeroDocumento).subscribe({
        next: (c) => {
          this.nombres = c.nombres;
          this.apellidos = c.apellidos;
        },
        error: (e) => this.errorDatos.set(mensajeDeError(e)),
      });
    }
  }

  protected guardarDatos(): void {
    const s = this.sesion.actual();
    if (!s) {
      return;
    }
    this.errorDatos.set('');
    this.exitoDatos.set('');
    this.api.actualizarCliente(s.tipoDocumento, s.numeroDocumento, this.nombres, this.apellidos).subscribe({
      next: () => this.exitoDatos.set('¡Datos guardados!'),
      error: (e) => this.errorDatos.set(mensajeDeError(e)),
    });
  }

  /** El backend cierra la sesión al cambiar la contraseña: hay que volver a entrar. */
  protected cambiarClave(): void {
    this.errorClave.set('');
    this.api.cambiarContrasena(this.actual, this.nueva).subscribe({
      next: () => {
        this.sesion.olvidar();
        this.router.navigate(['/entrar']);
      },
      error: (e) => this.errorClave.set(mensajeDeError(e)),
    });
  }
}
