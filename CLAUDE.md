# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Gym court reservation system (`reservas_gimnasio`). Spring Boot backend (Java 21, Maven, WAR packaging for Tomcat). Frontend not yet initialized.

## Backend Commands

All commands run from `backend/`:

```bash
# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ProyectoApplicationTests

# Build (skip tests)
./mvnw package -DskipTests

# Clean build
./mvnw clean package
```

## Architecture

**Package:** `com.reservas_gimnasio.proyecto`

Layered Spring MVC architecture:

- **`models/`** — JPA entities mapped to MySQL tables
- **`Repositories/`** — Spring Data JPA interfaces (extend `JpaRepository`)
- **`Services/`** — Business logic; injected into controllers
- **`Controllers/`** — REST endpoints; consume/return DTOs
- **`Dto/`** — `*RequestDTO` (incoming) and `*ResponseDTO` (outgoing); entities are never exposed directly
- **`Exceptions/`** — Custom exception classes

**Database:** MySQL in production; H2 in-memory for tests (configured via `application.properties`).

## Domain Model

Four core entities:

| Entity | Description |
|---|---|
| `Usuario` | App user; roles `USER` / `ADMIN` |
| `Pista` | Sports court; types `TENNIS`, `PADEL`, `FUTBOL` |
| `Reserva` | Booking linking a user to a court time slot; statuses `CONFIRMED`, `CANCELLED`, `COMPLETED` |
| `Bloqueo` | Court blockage for maintenance or events (prevents bookings) |

## Key Business Rules

1. No overlapping reservations on the same court.
2. A slot is only available if there are no existing `Reserva` or `Bloqueo` records for that court/time.
3. Max **3 active future reservations** per user.
4. Cancellations require **≥2 hours** notice before the reservation start.
5. Reservation duration: **1–2 hours**.
6. Users can only modify/cancel their own reservations; admins are unrestricted.
7. State transitions: `CONFIRMED → COMPLETED` or `CONFIRMED → CANCELLED` only.

## Development Phase

MVP scope: CRUD + validation only. Spring Security is a dependency but auth/authorization is **not** implemented in the initial phase — do not add security constraints to endpoints unless explicitly asked.


## Idioma y Contexto de respuesta

Hablame siempre en toda circunstancia y pregunta  en español de España.

Este proyecto esta orientado con fines didacticos, tengo una base como programador junior de los fundamentos de springboot, quiero que actues como un profesor. Mi carencia fundamental es que llevo mucho tiempo sin programar en springboot y necesitaré mucha ayuda con la sintaxis. Cuando notes que hay algun concepto que no entiendo o que no estoy siguiendo mandame investigar sobre lo que consideres necesario.

Referencias importantes para este proyecto los encuentras en @idea_bbdd.md y @idea_proyecto.md
