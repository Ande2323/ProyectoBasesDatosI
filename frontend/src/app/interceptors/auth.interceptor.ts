import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { SesionService } from '../services/sesion.service';

/** Envía el token en cada petición. Si la sesión venció (401), vuelve a la pantalla de entrada. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  const token = sesion.actual()?.token;
  const peticion = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && token) {
        sesion.olvidar();
        router.navigate(['/entrar']);
      }
      return throwError(() => error);
    }),
  );
};
