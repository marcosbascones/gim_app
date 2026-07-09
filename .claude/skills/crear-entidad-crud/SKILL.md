---
name: crear-entidad-crud
description: Usar cuando se pida crear una entidad JPA nueva con su CRUD completo (modelo, repository, DTOs, service, controller, tests) en este proyecto de Spring Boot.
---

Procedimiento seguido al construir la entidad `Reserva` paso a paso. Sirve como referencia para crear cualquier entidad nueva con su CRUD completo.

## 1. Orden de construcción

modelo → repository → DTOs → service (regla a regla, no todo de golpe) → controller → tests.

Cada paso se revisa y se commitea por separado antes de pasar al siguiente. No avanzar al siguiente paso sin cerrar el anterior.

## 2. Estilo del modelo y repository

Deben seguir el mismo estilo que las entidades ya existentes en el proyecto:
- Lombok para getters/setters/constructores.
- Convenciones de nombres ya establecidas en el proyecto.
- `@Enumerated(EnumType.STRING)` siempre, nunca `ORDINAL` (evita romper datos si se reordena el enum).

## 3. Alerta: métodos derivados de Spring Data JPA con dos cláusulas de fecha

En métodos tipo `findByXBeforeAndYAfter`, Spring Data enlaza los parámetros por **posición según el nombre del método**, no por el nombre de la variable en la firma Java. Es fácil pasar los argumentos en el orden equivocado sin que compile mal ni falle a simple vista.

- Comentar siempre en el propio método qué representa cada posición del método derivado.
- Verificar con al menos dos casos antes de darlo por bueno: uno que debe detectar solapamiento y otro que no debe.

## 4. RequestDTO no incluyen estado inicial decidido por el Service

Si el estado inicial de una entidad lo decide el Service (por ejemplo, una reserva nueva siempre nace `CONFIRMED`), ese campo no debe existir en el RequestDTO. No debe ser posible mandar `estado: CONFIRMED` desde fuera.

## 5. Orden de las reglas de negocio en el Service

Cada regla va en su propio bloque validado, y el orden importa:
1. Comprobaciones en memoria (más baratas) antes que consultas a base de datos (más caras).
2. Comprobaciones de autorización (quién puede hacer la acción) antes que las de negocio con matices sensibles (por ejemplo, plazos de tiempo tipo "cancelación con 2h de antelación"), para no filtrar información a alguien no autorizado sobre el estado del recurso antes de saber si tiene permiso.

## 6. Tests unitarios con Mockito

Nunca usar `any()` de forma indiscriminada en los mocks. Cada mock debe devolver el resultado específico que corresponde al escenario que ese test concreto está probando, para que el test falle si la lógica cambia de forma incorrecta.

## 7. Verificación de queries derivadas de JPA

El test unitario con mocks no es suficiente para verificar una query derivada (`findByXBeforeAndYAfter`, etc.), porque los mocks nunca ejecutan el SQL generado. Se necesita además un test de integración con `@DataJpaTest` que ejecute la query real contra H2.

## 8. Revisión antes de dar por terminado

Antes de dar cualquier pieza por terminada:
- Mostrar el código real (no un resumen) para su revisión.
- Verificar con casos límite concretos antes de commitear.

## 9. Deuda técnica

Cualquier deuda técnica o mejora aplazada se anota en `TODO.md` con el contexto suficiente para entenderla en el futuro sin necesidad de la conversación original.
