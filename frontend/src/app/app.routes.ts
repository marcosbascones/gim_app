import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'pistas',
    // loadComponent = lazy loading: el código de ListaPistas (y sus
    // dependencias) NO se incluye en el bundle inicial de la app, solo se
    // descarga cuando el usuario navega a /pistas. Mejora el tiempo de carga
    // inicial a medida que la app crece con más features.
    loadComponent: () => import('./features/pistas/lista-pistas').then((m) => m.ListaPistas),
  },
  { path: '', redirectTo: 'pistas', pathMatch: 'full' },
];
