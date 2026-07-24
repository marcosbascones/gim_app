# CLAUDE.md — Frontend (Angular)

> Contexto **anidado**: este fichero solo aplica al trabajar dentro de `frontend/`.
> Complementa (no reemplaza) al `CLAUDE.md` raíz del proyecto.

## Rol y modo de trabajo (IMPORTANTE — override del raíz)

En backend el desarrollador domina la arquitectura y solo quiere dirigir el agente.
**En frontend es distinto: es principiante en Angular.** Por tanto, aquí:

- Explicar **didácticamente** qué hace cada pieza: para qué sirve un componente,
  un servicio, un signal, una ruta, y qué hace cada método antes o después de escribirlo.
- Al introducir un concepto nuevo de Angular por primera vez (signals, `inject()`,
  control flow `@if`/`@for`, `HttpClient`, formularios reactivos...), dar una explicación
  breve del concepto, no solo el código.
- Seguir escribiendo el código directamente (no mandar a investigar sintaxis), pero
  acompañarlo del "qué" y el "porqué". El objetivo es que entienda la lógica de Angular,
  no que memorice anotaciones.

## Qué es este frontend

Frontend del sistema de reservas de pistas deportivas. Consumirá la API REST del backend
Spring Boot. Tendrá **dos formas de interacción** con el mismo sistema:

1. **Dashboard clásico** (fase actual): pantallas para ver pistas, ver disponibilidad,
   crear/cancelar reservas, y (admin) gestionar pistas y usuarios.
2. **Chatbot conversacional** (fase futura): asistente en lenguaje natural que ejecuta
   las mismas acciones vía function calling contra los endpoints existentes. **No construir
   hasta que el dashboard funcione.** Restricción de seguridad: el chat nunca puede crear
   ni promocionar usuarios ADMIN.

## Stack

- **Angular 21** — standalone components, **signals**, control flow nativo (`@if`/`@for`/`@switch`).
- **Tailwind CSS v4** (ya instalado, config CSS-first vía `@import "tailwindcss"` y `@theme`).
- **Vitest** para tests.
- Sin librería de estado externa: signals + servicios `providedIn: 'root'`.

## API del backend

- **Base URL (dev):** `http://localhost:8080` (definir en `environment.ts` como `apiUrl`).
- ⚠️ **CORS pendiente en el backend**: hasta que se habilite `@CrossOrigin`/config CORS
  para `http://localhost:4200`, las peticiones fallarán en el navegador. Es lo primero a resolver.
- Fechas: viajan como ISO-8601 (`LocalDateTime` → `"2026-02-12T18:00:00"`).

| Recurso | Método | Ruta | Cuerpo (Request) → Respuesta |
|---|---|---|---|
| Pistas | GET | `/pistas` | → `PistaResponse[]` |
| Pistas | GET | `/pistas/{id}` | → `PistaResponse` |
| Pistas | POST | `/pistas` | `PistaRequest` → `PistaResponse` (201) |
| Pistas | PUT | `/pistas/{id}` | `PistaRequest` → `PistaResponse` |
| Pistas | DELETE | `/pistas/{id}` | → 204 |
| Usuarios | GET | `/usuarios` | → `UsuarioResponse[]` |
| Usuarios | GET | `/usuarios/{id}` | → `UsuarioResponse` |
| Usuarios | POST | `/usuarios` | `UsuarioRequest` → `UsuarioResponse` (201) |
| Usuarios | PUT | `/usuarios/{id}` | `UsuarioRequest` → `UsuarioResponse` |
| Usuarios | DELETE | `/usuarios/{id}` | → 204 |
| Reservas | POST | `/reservas` | `ReservaRequest` → `ReservaResponse` (201) |
| Reservas | DELETE | `/reservas/{id}?usuarioSolicitanteId={id}` | → `ReservaResponse` (cancelada) |

**Forma de los DTOs** (las interfaces TS en `core/models/` deben reflejarlos):

- `PistaRequest`  = `{ nombre: string; deporte: 'TENIS'|'PADEL'|'FUTBOL'; activa: boolean }`
- `PistaResponse` = `{ id: number } &` lo anterior
- `UsuarioRequest`  = `{ nombre; email; password; rol: 'USER'|'ADMIN' }`
- `UsuarioResponse` = `{ id; email; nombre; estado: 'ACTIVO'|'BLOQUEADO'; rol }` (sin password)
- `ReservaRequest`  = `{ usuarioId; pistaId; fechaHoraInicio; fechaHoraFin }` (ISO strings)
- `ReservaResponse` = `{ id; usuarioId; usuarioNombre; pistaId; pistaNombre; fechaHoraInicio; fechaHoraFin; estado: 'CONFIRMED'|'CANCELLED'|'COMPLETED' }`

### Huecos conocidos del backend (frontend los necesitará)
- ❌ **No hay `GET /reservas`** ni "mis reservas": el dashboard lo necesita. Añadir en backend.
- ❌ **No hay disponibilidad/slots** como endpoint: hoy se calcularía en cliente o hace falta añadirlo.
- ⚠️ **Sin auth**: `cancelarReserva` recibe `usuarioSolicitanteId` por query param. El frontend
  tendrá que gestionar un "usuario actual" (seleccionado/simulado) hasta que exista login.

## Estructura de carpetas (convención)

```
src/app/
  core/                # singletons transversales
    models/            # interfaces TS que reflejan los DTOs del backend
    services/          # PistaService, ReservaService, UsuarioService (HttpClient)
  features/            # una carpeta por área funcional
    pistas/  reservas/  usuarios/  chatbot/
  shared/              # componentes reutilizables (boton, card, badge-estado...)
  layout/              # shell de la app, navbar
```

- `provideHttpClient()` va en `app.config.ts` (todavía no está: añadir al configurar la base).
- Rutas de features con **lazy loading** (`loadComponent`/`loadChildren`).

## Sistema de diseño (Tailwind v4)

Definir los tokens en `styles.css` con `@theme`. Propuesta base (sobria, temática deportiva):

- **Primario:** emerald (`emerald-600` acción principal, `emerald-700` hover). Evoca cancha/deporte.
- **Neutrales:** `slate` (fondos `slate-50`, texto `slate-800`, bordes `slate-200`).
- **Badges de estado de reserva:** `CONFIRMED`→emerald, `COMPLETED`→slate, `CANCELLED`→red.
- **Deportes:** color/emoji distinto por deporte para reconocerlos de un vistazo.
- **Formas:** `rounded-lg`, sombras suaves (`shadow-sm`), espaciado generoso.
- **Tipografía:** sans del sistema (o Inter). Jerarquía clara con pesos, no con muchos tamaños.

Componentes base a estandarizar en `shared/`: botón (primario/secundario/peligro), card,
badge de estado, input de formulario. Se consolidarán en una skill de estilo más adelante.

---

# Buenas prácticas de Angular / TypeScript (mantener)

## TypeScript
- Strict type checking. Preferir inferencia cuando el tipo es obvio.
- Evitar `any`; usar `unknown` si el tipo es incierto.

## Angular
- Standalone components (por defecto en v21; **no** poner `standalone: true`).
- **Signals** para estado; `computed()` para estado derivado. Nunca `mutate`: usar `set`/`update`.
- Lazy loading en rutas de feature.
- No usar `@HostBinding`/`@HostListener`: usar el objeto `host` del decorador.
- `NgOptimizedImage` para imágenes estáticas (no sirve para base64 inline).

## Componentes
- Pequeños y de responsabilidad única.
- `input()` y `output()` como funciones, no decoradores.
- `changeDetection: ChangeDetectionStrategy.OnPush`.
- Plantillas inline para componentes pequeños.
- Formularios **reactivos**, no template-driven.
- Bindings `class`/`style`, **no** `ngClass`/`ngStyle`.

## Plantillas
- Control flow nativo (`@if`/`@for`/`@switch`), no `*ngIf`/`*ngFor`/`*ngSwitch`.
- Pipe `async` para observables. No asumir globales (`new Date()`).

## Servicios
- Responsabilidad única. `providedIn: 'root'` para singletons.
- Inyección con `inject()`, no por constructor.

## Accesibilidad
- Cumplir WCAG AA (contraste, foco, ARIA) y pasar checks de AXE.
