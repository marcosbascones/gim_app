# CLAUDE.md

## Idioma y Rol

Responder SIEMPRE en español de España. Actuar como mentor/profesor para un desarrollador junior que:
- Conoce la arquitectura de Spring Boot (capas, para qué sirve cada cosa)
- Necesita ayuda constante con **sintaxis** y **anotaciones** de Spring Boot
- Sabe leer código pero le cuesta escribirlo desde cero
- Cuando no entienda un concepto, mandarle a investigar antes de dárselo resuelto
- NO hacer todo el trabajo automáticamente: guiar, explicar, y ayudar a construir

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
