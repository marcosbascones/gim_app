# 🧭 Ruta de aprendizaje

> Documento **vivo** y personal del desarrollador (no forma parte del spec del producto).
> Objetivo real de este proyecto: **aprender a dirigir agentes de IA en programación**.
> El sistema de reservas es el vehículo de práctica, no el fin.

Estados: ✅ hecho · 🔄 en marcha · ⬜ pendiente

## Carril A — Dirigir agentes de IA (objetivo principal)

- 🔄 **Gestión de contexto y tokens** — CLAUDE.md anidados (raíz + frontend), uso de `/context`, evitar ruido. Pendiente: extraer `backend/CLAUDE.md` cuando compense (ver TODO.md).
- ⬜ **Git / GitHub con el agente** — commits guiados, ramas, PRs, sincronización.
- 🔄 **Skills** — crear y, sobre todo, *destilar* skills a partir de trabajo real. Hechas: `crear-entidad-crud`. Pendientes: `crear-componente-angular`, `crear-servicio-api`, `estilo-tailwind`.
- ⬜ **MCP** — servidor MySQL read-only ya configurado; falta usarlo con soltura.
- ⬜ **Multiagente / subagentes** — repartir trabajo entre agentes, cuándo compensa y cuándo no.
- 🔄 **Buenas prácticas de dirección** — prompts claros, contrastar contra fuentes de verdad, revisar el código antes de darlo por bueno.

## Carril B — Angular / Frontend (didáctico, secundario)

- ⬜ **Fundamentos Angular** — componentes standalone, signals, servicios, `inject()`, routing, control flow nativo.
- ⬜ **HttpClient y consumo de API** — servicios tipados que reflejan los DTOs del backend.
- ⬜ **Formularios reactivos**.
- ⬜ **Tailwind v4 / sistema de diseño** — tokens con `@theme`, componentes base reutilizables.
- ⬜ **Chatbot con function calling** (futuro) — LLM que invoca los endpoints existentes como herramientas.

## Cómo se usa este fichero

- Cada hito del proyecto suele ser también práctica de algún punto de arriba: anotarlo aquí al cerrarlo y actualizar su estado.
- No es un temario rígido ni un examen: es un mapa para ver el progreso y decidir qué practicar después.
