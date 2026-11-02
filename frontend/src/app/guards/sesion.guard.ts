import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SesionService } from '../services/sesion.service';

export const conSesion: CanActivateFn = () => {
  return inject(SesionService).actual() ? true : inject(Router).createUrlTree(['/entrar']);
};

export const sinSesion: CanActivateFn = () => {
  const sesion = inject(SesionService);
  return sesion.actual() ? inject(Router).createUrlTree([sesion.inicio()]) : true;
};

export const soloPersonal: CanActivateFn = () => {
  const sesion = inject(SesionService);
  if (!sesion.actual()) {
    return inject(Router).createUrlTree(['/entrar']);
  }
  return sesion.esCliente() ? inject(Router).createUrlTree(['/catalogo']) : true;
};

export const soloAdmin: CanActivateFn = () => {
  const sesion = inject(SesionService);
  if (!sesion.actual()) {
    return inject(Router).createUrlTree(['/entrar']);
  }
  return sesion.esAdmin() ? true : inject(Router).createUrlTree([sesion.inicio()]);
};

export const inicioSegunRol: CanActivateFn = () => {
  const sesion = inject(SesionService);
  return inject(Router).createUrlTree([sesion.actual() ? sesion.inicio() : '/entrar']);
};
