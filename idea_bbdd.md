# 🗄️ 🏟️ Sistema de Reservas de Club Deportivo — Base de Datos (ERD)

---

# 📊 1. Diagrama entidad-relación (ERD)

```text
┌───────────────┐
│   Usuario     │
├───────────────┤
│ id (PK)       │
│ nombre        │
│ email (UNIQUE)│
│ password      │
│ rol           │
│ estado        │
└──────┬────────┘
       │ 1
       │
       │ N
┌──────▼────────┐
│   Reserva     │
├───────────────┤
│ id (PK)       │
│ usuario_id FK │──────────────┐
│ pista_id FK   │              │
│ fecha         │              │
│ hora_inicio   │              │
│ hora_fin      │              │
│ estado        │              │
└──────┬────────┘              │
       │ N                    │ 1
       ▼                     │
┌───────────────┐           │
│    Pista      │           │
├───────────────┤           │
│ id (PK)       │           │
│ nombre        │           │
│ deporte       │           │
│ activa        │           │
└──────┬────────┘           │
       │ 1                 │
       │                  │
       │ N                │
┌──────▼────────┐        │
│   Bloqueo     │        │
├───────────────┤        │
│ id (PK)       │        │
│ pista_id FK   │────────┘
│ inicio        │
│ fin           │
│ motivo        │
└───────────────┘

-USUARIO

id (PK)
nombre
email (UNIQUE)
password
rol (USER / ADMIN)
estado (ACTIVO / BLOQUEADO)

-PISTA

id (PK)
nombre
deporte (TENIS / PADEL / FUTBOL)
activa (boolean)

-RESERVA

id (PK)
usuario_id (FK → Usuario)
pista_id (FK → Pista)
fecha
hora_inicio
hora_fin
estado:
  - PENDING
  - CONFIRMED
  - CANCELLED
  - COMPLETED


-BLOQUEO PISTA

id (PK)
pista_id (FK → Pista)
inicio
fin
motivo (mantenimiento, lluvia, evento, etc.)


👤 Usuario → Reserva (1:N) Usuario 1 ───────── N Reserva Un usuario puede tener muchas reservas Cada reserva pertenece a un único usuario 
🏟️ Pista → Reserva (1:N) Pista 1 ───────── N Reserva Una pista puede tener múltiples reservas en el tiempo Cada reserva pertenece a una sola pista 
🏟️ Pista → Bloqueo (1:N) Pista 1 ───────── N Bloqueo Una pista puede tener múltiples bloqueos Ej: mantenimiento, eventos, lluvia