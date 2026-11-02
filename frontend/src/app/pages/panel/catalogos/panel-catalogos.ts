import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Fila } from '../../../models/modelos';
import { PaginaPanel } from '../pagina-panel';

interface Campo {
  clave: string;
  titulo: string;
  tipo: 'text' | 'number' | 'date' | 'nivel';
  paso?: string;
}

interface Catalogo {
  ruta: string;
  titulo: string;
  llave: string;
  llaveEditable?: boolean;
  basico?: boolean;
  campos: Campo[];
}

const CATALOGOS: Catalogo[] = [
  { ruta: 'colores', titulo: 'Colores', llave: 'idColor', basico: true, campos: [
    { clave: 'nombreColor', titulo: 'Nombre', tipo: 'text' },
    { clave: 'ordenCanonico', titulo: 'Orden', tipo: 'number' },
  ] },
  { ruta: 'tipos', titulo: 'Tipos', llave: 'idTipo', basico: true, campos: [
    { clave: 'nivel', titulo: 'Nivel', tipo: 'nivel' },
    { clave: 'codigo', titulo: 'Código', tipo: 'text' },
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
  ] },
  { ruta: 'ediciones', titulo: 'Ediciones', llave: 'idEdicion', campos: [
    { clave: 'codigoImpreso', titulo: 'Código', tipo: 'text' },
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
    { clave: 'fechaLanzamiento', titulo: 'Lanzamiento', tipo: 'date' },
    { clave: 'cantidadCartas', titulo: 'Cartas', tipo: 'number' },
    { clave: 'tipoEdicion', titulo: 'Tipo', tipo: 'text' },
  ] },
  { ruta: 'idiomas', titulo: 'Idiomas', llave: 'idIdioma', basico: true, campos: [
    { clave: 'codigo', titulo: 'Código', tipo: 'text' },
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
  ] },
  { ruta: 'rarezas', titulo: 'Rarezas', llave: 'idRareza', basico: true, campos: [
    { clave: 'codigo', titulo: 'Código', tipo: 'text' },
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
  ] },
  { ruta: 'artistas', titulo: 'Artistas', llave: 'idArtista', campos: [
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
  ] },
  { ruta: 'condiciones', titulo: 'Condiciones', llave: 'codigoCondicion', llaveEditable: true, basico: true, campos: [
    { clave: 'codigoCondicion', titulo: 'Código', tipo: 'text' },
    { clave: 'nombre', titulo: 'Nombre', tipo: 'text' },
    { clave: 'factorAjuste', titulo: 'Factor (0 a 1)', tipo: 'number', paso: '0.001' },
  ] },
];

const NIVELES: Record<number, string> = { 1: 'Supertipo', 2: 'Tipo', 3: 'Subtipo' };

/** Los siete catálogos son iguales en forma: una sola página los maneja con esta configuración. */
@Component({
  selector: 'app-panel-catalogos',
  imports: [FormsModule],
  templateUrl: './panel-catalogos.html',
})
export class PanelCatalogos extends PaginaPanel implements OnInit {
  protected readonly catalogos = CATALOGOS;
  protected readonly actual = signal(CATALOGOS[0]);
  protected readonly filas = signal<Fila[]>([]);
  protected readonly cargando = signal(true);
  protected readonly llaveEditando = signal<unknown>(null);
  protected forma: Fila = {};

  protected readonly camposForma = computed(() => {
    const c = this.actual();
    return c.campos.filter((campo) => !(campo.clave === c.llave && this.llaveEditando() !== null));
  });

  ngOnInit(): void {
    this.cargar();
  }

  protected elegir(c: Catalogo): void {
    this.actual.set(c);
    this.limpiar();
    this.error.set('');
    this.exito.set('');
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.api.listar(this.actual().ruta).subscribe({
      next: (f) => {
        this.filas.set(f);
        this.cargando.set(false);
      },
      error: (e) => {
        this.mostrarError(e);
        this.cargando.set(false);
      },
    });
  }

  protected limpiar(): void {
    this.forma = {};
    this.llaveEditando.set(null);
  }

  protected editar(fila: Fila): void {
    this.forma = { ...fila };
    this.llaveEditando.set(fila[this.actual().llave]);
  }

  protected guardar(): void {
    const c = this.actual();
    const llave = this.llaveEditando();
    const datos: Fila = {};
    for (const campo of c.campos) {
      const valor = this.forma[campo.clave];
      datos[campo.clave] = valor === '' || valor === undefined ? null : valor;
    }
    const accion = llave === null ? this.api.crear(c.ruta, datos) : this.api.actualizar(c.ruta, llave, datos);
    this.ejecutar<unknown>(accion, llave === null ? '¡Registro creado!' : 'Registro actualizado.', () => {
      this.limpiar();
      this.cargar();
    });
  }

  protected eliminar(fila: Fila): void {
    if (!confirm('¿Eliminar este registro? Solo se puede si nada lo usa.')) {
      return;
    }
    this.ejecutar(this.api.eliminar(this.actual().ruta, fila[this.actual().llave]), 'Registro eliminado.', () => this.cargar());
  }

  protected mostrar(fila: Fila, campo: Campo): unknown {
    return campo.tipo === 'nivel' ? NIVELES[fila[campo.clave] as number] : fila[campo.clave];
  }
}
