import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { PistaService } from '../../core/services/pista.service';
import { PistaResponse } from '../../core/models/pista.model';

@Component({
  selector: 'app-lista-pistas',
  // OnPush: Angular solo repinta este componente cuando cambia un signal
  // que usa la plantilla (o cambian sus @input). Es más eficiente que el
  // modo por defecto, que revisa todo el árbol de componentes constantemente.
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h1 class="text-xl font-semibold text-slate-800 mb-4">Pistas</h1>

    <!--
      @if / @else es el "control flow" nativo de Angular (sustituye a *ngIf).
      Se lee como una cadena de estados: mientras carga, muestra "Cargando...";
      si hubo error, el mensaje de error; si no, la lista.
    -->
    @if (cargando()) {
      <p class="text-slate-500">Cargando pistas...</p>
    } @else if (error()) {
      <p class="text-red-600">{{ error() }}</p>
    } @else {
      <ul class="space-y-2">
        <!--
          @for recorre el array que hay dentro del signal "pistas".
          "track pista.id" es OBLIGATORIO: le dice a Angular cómo identificar
          cada elemento entre repintados, para no destruir y recrear el DOM
          de cada <li> cuando solo cambia un elemento de la lista.
        -->
        @for (pista of pistas(); track pista.id) {
          <li class="rounded-lg border border-slate-200 shadow-sm p-3">
            <span class="font-medium">{{ pista.nombre }}</span>
            <span class="text-slate-500"> — {{ pista.deporte }}</span>
            @if (!pista.activa) {
              <span class="text-red-600 text-sm"> (inactiva)</span>
            }
          </li>
        }
      </ul>
    }
  `,
})
export class ListaPistas implements OnInit {
  private pistaService = inject(PistaService);

  // Un signal es una "caja" reactiva que guarda un valor. Cuando cambia
  // (con .set() o .update()), Angular sabe automáticamente que la plantilla
  // que lo lee (pistas(), con paréntesis) debe repintarse. No hace falta
  // avisar manualmente al framework como con setState en otros frameworks.
  pistas = signal<PistaResponse[]>([]);
  cargando = signal(true);
  error = signal<string | null>(null);

  // ngOnInit es un "hook" del ciclo de vida del componente: Angular lo llama
  // automáticamente justo después de crear el componente. Es el sitio correcto
  // para lanzar la petición inicial (no en el constructor, que es solo para
  // configurar cosas, no para efectos como llamadas HTTP).
  ngOnInit(): void {
    this.pistaService.getPistas().subscribe({
      // next: se ejecuta si el Observable emite datos correctamente.
      next: (data) => {
        this.pistas.set(data);
        this.cargando.set(false);
      },
      // error: se ejecuta si la petición HTTP falla (backend caído, CORS, 500...).
      error: () => {
        this.error.set('No se han podido cargar las pistas.');
        this.cargando.set(false);
      },
    });
  }
}
