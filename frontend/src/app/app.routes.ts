import { Routes } from '@angular/router';

import { conSesion, inicioSegunRol, sinSesion, soloAdmin, soloPersonal } from './guards/sesion.guard';
import { Carta } from './pages/carta/carta';
import { Catalogo } from './pages/catalogo/catalogo';
import { Entrar } from './pages/entrar/entrar';
import { MisReservas } from './pages/mis-reservas/mis-reservas';
import { PanelCartas } from './pages/panel/cartas/panel-cartas';
import { PanelCatalogos } from './pages/panel/catalogos/panel-catalogos';
import { PanelEstadisticas } from './pages/panel/estadisticas/panel-estadisticas';
import { PanelInventario } from './pages/panel/inventario/panel-inventario';
import { PanelLayout } from './pages/panel/panel-layout/panel-layout';
import { PanelPrecios } from './pages/panel/precios/panel-precios';
import { PanelReservas } from './pages/panel/reservas/panel-reservas';
import { PanelSalidas } from './pages/panel/salidas/panel-salidas';
import { PanelUsuarios } from './pages/panel/usuarios/panel-usuarios';
import { Perfil } from './pages/perfil/perfil';
import { Registro } from './pages/registro/registro';

export const routes: Routes = [
  { path: '', pathMatch: 'full', canActivate: [inicioSegunRol], children: [] },
  { path: 'entrar', component: Entrar, canActivate: [sinSesion] },
  { path: 'registro', component: Registro, canActivate: [sinSesion] },
  { path: 'catalogo', component: Catalogo, canActivate: [conSesion] },
  { path: 'carta/:id', component: Carta, canActivate: [conSesion] },
  { path: 'mis-reservas', component: MisReservas, canActivate: [conSesion] },
  { path: 'perfil', component: Perfil, canActivate: [conSesion] },
  {
    path: 'panel',
    component: PanelLayout,
    canActivate: [soloPersonal],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inventario' },
      { path: 'inventario', component: PanelInventario },
      { path: 'reservas', component: PanelReservas },
      { path: 'salidas', component: PanelSalidas },
      { path: 'precios', component: PanelPrecios },
      { path: 'cartas', component: PanelCartas },
      { path: 'estadisticas', component: PanelEstadisticas },
      { path: 'catalogos', component: PanelCatalogos, canActivate: [soloAdmin] },
      { path: 'usuarios', component: PanelUsuarios, canActivate: [soloAdmin] },
    ],
  },
  { path: '**', canActivate: [inicioSegunRol], children: [] },
];
