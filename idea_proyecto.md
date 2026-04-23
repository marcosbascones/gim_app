# 🏟️ Sistema de Reservas de Club Deportivo

## 📌 1. Descripción general

Este proyecto consiste en una aplicación web para la gestión de reservas de pistas deportivas (tenis, pádel, fútbol, etc.).

La disponibilidad de pistas se mostrará en el frontend como una serie de franjas horarias (“slots”) calculadas dinámicamente a partir de las reservas existentes y bloqueos de pista.

Los usuarios podrán consultar disponibilidad y reservar franjas horarias, mientras que los administradores gestionan usuarios, pistas y horarios.

El sistema está diseñado para garantizar la consistencia de las reservas y evitar conflictos de disponibilidad.

---

## 👥 2. Roles del sistema

### 🧑 Usuario (ROLE_USER)

Puede:
- Ver pistas disponibles
- Ver horarios disponibles
- Crear reservas
- Cancelar sus propias reservas
- Consultar sus reservas

Restricciones:
- No puede modificar reservas de otros usuarios
- No puede gestionar pistas ni usuarios

---

### 👑 Administrador (ROLE_ADMIN)

Puede:
- Todas las acciones del usuario
- Crear, editar y eliminar usuarios
- Crear, editar y eliminar pistas
- Gestionar horarios de pistas
- Cancelar cualquier reserva
- Bloquear pistas o franjas horarias

---

## 🧱 3. Entidades principales

### 🧍 Usuario
- id
- nombre
- email
- password
- rol (USER / ADMIN)
- estado (ACTIVO / BLOQUEADO)

---

### 🏟️ Pista
- id
- nombre (ej: “Pista 1”)
- deporte (TENIS, PADEL, FUTBOL)
- activa (boolean)

---

### 📅 Reserva
- id
- usuario
- pista
- fechaHoraInicio (LocalDateTime)
- fechaHoraFin (LocalDateTime)
- estado:
  
  - CONFIRMED
  - CANCELLED
  - COMPLETED

---

### ⛔ Bloqueo de pista
- id
- pista
- fechaHoraInicio
- fechaHoraFin
- motivo (mantenimiento, lluvia, evento, etc.)

---

## 🧠 4. Reglas de negocio (CORE DEL SISTEMA)

### 🔴 R1 — No solapamiento de reservas
No pueden existir dos reservas en la misma pista cuyos horarios se solapen.

Ejemplo inválido:
- 10:00 – 11:00
- 10:30 – 11:30 ❌

---

### 🔴 R2 — Validación de disponibilidad
No se puede crear una reserva si:
- Existe otra reserva en ese horario
- La pista está bloqueada

---

### 🔴 R3 — Límite de reservas por usuario
Un usuario solo puede tener un número limitado de reservas activas futuras.

MVP: máximo 3 reservas activas futuras.

El conteo incluye únicamente reservas en estado CONFIRMED con fecha futura.
Las reservas CANCELLED o COMPLETED no computan para este límite.

---

### 🔴 R4 — Cancelación con restricción temporal
Un usuario no puede cancelar una reserva si faltan menos de 2 horas para su inicio.

El administrador sí puede cancelar en cualquier momento.

---

### 🔴 R5 — Estados de reserva
Una reserva puede estar en los siguientes estados:

- Las reservas una vez realizadas llevarán el estado CONFIRMED de inicio.
- Una vez pasada la fecha para la que fue creada, pasará automáticamente a COMPLETED.
- Si es cancelada pasará a CANCELLED.

Estados posibles:
- CONFIRMED (validada al crearse)
- CANCELLED (cancelada)
- COMPLETED (finalizada)

Solo se pueden cancelar reservas en estado CONFIRMED. Intentar cancelar una
reserva COMPLETED o CANCELLED debe lanzar una excepción de negocio.

---

### 🔴 R6 — Duración de reservas
Las reservas deben cumplir:

- Duración mínima: 1 hora
- Duración máxima: 2 horas

---

### 🔴 R7 — Propiedad de reservas
Un usuario solo puede:
- Ver sus propias reservas
- Cancelar sus propias reservas (si cumple las reglas)

---

## 🧪 5. Estrategia de validación

Todas las reglas de negocio deben implementarse en la capa de servicio:

- ❌ No en controllers
- ❌ No en frontend
- ✅ En Service Layer

---

## 🧪 6. Estrategia de testing

### ✔ Unit Tests
Usando Mockito:
- Validación de solapamiento de reservas
- Límite de reservas por usuario
- Restricciones de cancelación
- Reglas de negocio en servicios

---

### ✔ Integration Tests
- Endpoints completos funcionando
- Interacción con base de datos
- Flujo completo usuario → reserva → persistencia

---

## 🧱 7. Arquitectura del backend

Estructura recomendada:

controller/
service/
repository/
model/
dto/
exception/
security/


---

## 🔐 8. Seguridad (fase posterior)

Se implementará en una fase avanzada:

- Autenticación con Spring Security
- Login de usuarios
- JWT para sesiones
- Roles (USER / ADMIN)
- Protección de endpoints

---

## 🚀 9. MVP (PRIMERA VERSIÓN)

El sistema inicial incluirá:

- Gestión de usuarios
- Gestión de pistas
- Creación de reservas
- Validación de solapamientos
- Límite de reservas por usuario
- Cancelación con reglas básicas

---

## 🌱 10. Funcionalidades futuras (NO incluidas en MVP)

- Lista de espera para reservas
- Sistema de pagos
- Estadísticas de uso
- Notificaciones (email / push)
- App móvil
- Geolocalización de instalaciones
- Sistema de fidelización de usuarios

---

## 🎯 Objetivo del proyecto

Demostrar conocimientos en:

- Desarrollo backend con Spring Boot
- Diseño de APIs REST
- Arquitectura en capas
- Lógica de negocio compleja
- Testing unitario e integración
- Diseño de sistemas reales
- Seguridad básica con roles

---

## 🏁 Nota final

Este proyecto no es un CRUD simple, sino un sistema de gestión de reservas con reglas de negocio reales y escalables, orientado a simular un entorno de producción.
