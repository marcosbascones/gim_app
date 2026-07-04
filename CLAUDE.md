# CLAUDE.md

## Idioma y Rol

Responder SIEMPRE en español de España.

El desarrollador está usando este proyecto para aprender a **dirigir agentes de IA en programación**, no para aprender los fundamentos de Spring Boot desde cero. Ya conoce la arquitectura en capas y para qué sirve cada una. El foco de aprendizaje actual es: gestión de contexto y tokens, uso de MCP, sincronización con Git/GitHub, skills, multiagente, y buenas prácticas de dirección de un asistente de código.

En consecuencia:
- Escribir el código directamente cuando se pida, sin obligar a investigar sintaxis de Spring Boot antes de dar la solución.
- Explicar brevemente QUÉ se ha hecho y POR QUÉ (decisiones de diseño, reglas de negocio aplicadas), no CÓMO funciona cada anotación línea a línea, salvo que se pida explícitamente.
- Si se detecta un bug, una inconsistencia con las reglas de negocio, o una decisión de arquitectura importante, señalarlo siempre y explicarlo con claridad — pero sin convertirlo en un ejercicio de investigación obligatorio. El desarrollador decide si quiere profundizar o que se resuelva directamente.
- No dar por hecho que el código propuesto es correcto solo porque compila: contrastarlo siempre contra `idea_proyecto.md` / `idea_bbdd.md` y las reglas de negocio de este documento.
- Ser proactivo señalando dónde una tarea es buena ocasión para practicar algo de la ruta de aprendizaje (context management, git, MCP, skills, multiagente) cuando aplique de forma natural, sin forzarlo.

## Project Overview

Sistema de reservas de pistas deportivas. Spring Boot backend (Java 17, Maven, WAR para Tomcat). Frontend previsto con Angular (fase futura).

**Package base:** `com.reservas_gimnasio.proyecto`

## Backend Commands

Ejecutar desde `backend/`:

```bash
./mvnw spring-boot:run          # Arrancar
./mvnw test                     # Tests
./mvnw test -Dtest=NombreTest   # Test individual
./mvnw clean package -DskipTests # Build limpio
```

## Architecture

Capas Spring MVC:

- **`models/`** — Entidades JPA (tablas MySQL)
- **`Repositories/`** — Interfaces JpaRepository
- **`Services/`** — Lógica de negocio (TODAS las reglas van aquí)
- **`Controllers/`** — Endpoints REST, solo reciben/devuelven DTOs
- **`Dto/`** — `*RequestDTO` (entrada) y `*ResponseDTO` (salida). Nunca exponer entidades
- **`Exceptions/`** — Excepciones de negocio personalizadas

**BD:** MySQL en producción; H2 en memoria para tests.

## Domain Model

| Entidad | Descripción |
|---|---|
| `Usuario` | Roles: `USER` / `ADMIN`. Estados: `ACTIVO` / `BLOQUEADO` |
| `Pista` | Deportes: `TENIS`, `PADEL`, `FUTBOL`. Campo `activa` (boolean) |
| `Reserva` | Liga usuario + pista + franja horaria. Estados: `CONFIRMED` / `CANCELLED` / `COMPLETED` |
| `Bloqueo` | Bloqueo de pista (mantenimiento, eventos). Impide reservas |

Relaciones: Usuario 1:N Reserva, Pista 1:N Reserva, Pista 1:N Bloqueo.

**Fuente de verdad del modelo:** `idea_proyecto.md` e `idea_bbdd.md`. Consultar siempre estos ficheros antes de crear o modificar entidades — la tabla de arriba es solo un resumen.

## Business Rules (MVP)

1. **No solapamiento** de reservas en la misma pista
2. **Disponibilidad**: slot libre = sin Reserva ni Bloqueo en ese horario
3. **Max 3 reservas activas futuras** por usuario (solo CONFIRMED con fecha futura)
4. **Cancelación**: minimo 2h antes del inicio (admins sin restricción)
5. **Duración**: entre 1 y 2 horas
6. **Propiedad**: usuarios solo modifican/cancelan las suyas; admins sin restricción
7. **Transiciones**: solo `CONFIRMED -> COMPLETED` o `CONFIRMED -> CANCELLED`

## Development Phase

MVP: CRUD + validaciones de negocio. Spring Security es dependencia pero **NO implementar auth/authorization** salvo petición explícita.

## Convenciones

- Clases/métodos: `CamelCase`
- Columnas BD: `snake_case`
- Logs: usar `Logger` (SLF4J) en Services para trazar operaciones y errores
- Errores API: excepciones de negocio capturadas y devueltas con códigos HTTP apropiados
