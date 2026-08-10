import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PistaResponse } from '../models/pista.model';

// providedIn: 'root' -> Angular crea UNA sola instancia de este servicio
// para toda la app (singleton), igual que un @Service de Spring es un bean único.
// Así cualquier componente que necesite pistas usa siempre la misma instancia,
// en vez de crear clientes HTTP repetidos por cada componente.
@Injectable({ providedIn: 'root' })
export class PistaService {
  // inject() es la forma moderna de pedir una dependencia dentro del cuerpo
  // de la clase (en vez de recibirla por constructor, como @Autowired en Spring).
  // Angular resuelve HttpClient desde su contenedor de inyección.
  private http = inject(HttpClient);

  // environment.apiUrl cambia según el modo: en desarrollo (ng serve) apunta
  // a http://localhost:8080; en producción se sustituye por environment.ts.
  private baseUrl = `${environment.apiUrl}/pistas`;

  // Devuelve un Observable, NO la lista directamente. Un Observable es una
  // "receta" para hacer la petición: no se ejecuta nada hasta que alguien
  // hace .subscribe() sobre él. Esto permite que el servicio solo describa
  // QUÉ petición hacer, y que el componente decida CUÁNDO suscribirse y
  // qué hacer con la respuesta (guardarla, mostrar error, etc).
  getPistas(): Observable<PistaResponse[]> {
    return this.http.get<PistaResponse[]>(this.baseUrl);
  }
}
