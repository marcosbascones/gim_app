# TODO

- Añadir test de integración con @DataJpaTest para BloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter, verificando casos de solapamiento parcial (ej. 09:00-11:00 vs 10:00-12:00). El test unitario actual usa any() en los mocks y no habría detectado el bug de orden de parámetros corregido en el commit fix: corrige orden de parámetros en query de solapamiento de Bloqueo.

## Tests pendientes de Reserva

- Test de integración con @DataJpaTest para ReservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter, con el mismo enfoque que el de Bloqueo: verificar solapamiento parcial real contra BD, no con mocks con any().
- Test de integración con @DataJpaTest para ReservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter (R3): comprobar que solo cuenta CONFIRMED futuras y que ignora CANCELLED/COMPLETED o reservas ya pasadas.
- Test de integración con @DataJpaTest para ReservaRepository.findByEstadoAndFechaHoraFinBefore (R5 automático): comprobar que solo devuelve CONFIRMED con fechaHoraFin pasada.
- Tests unitarios de ReservaService.crearReserva:
  - Fecha de inicio en el pasado o igual a "ahora" → ReglaNegocioException.
  - Duración < 1h o > 2h (R6) → ReglaNegocioException.
  - Usuario con 3 reservas activas futuras (R3) → ReglaNegocioException; con 2 → se crea sin problema.
  - Solapamiento con otra reserva no cancelada (R1) → ReglaNegocioException; solapamiento solo con una CANCELLED → no bloquea.
  - Solapamiento con un bloqueo (R2) → ReglaNegocioException.
  - Caso feliz: se guarda con estado CONFIRMED y el DTO de respuesta es correcto.
- Tests unitarios de ReservaService.cancelarReserva:
  - Reserva no encontrada → excepción.
  - Reserva no CONFIRMED (ya CANCELLED o COMPLETED) → ReglaNegocioException (R5).
  - Usuario solicitante que no es ni el dueño ni ADMIN → ReglaNegocioException (R7).
  - Usuario normal (dueño) cancelando con menos de 2h de antelación → ReglaNegocioException (R4).
  - Usuario normal (dueño) cancelando con más de 2h de antelación → se cancela correctamente.
  - ADMIN cancelando con menos de 2h de antelación → se cancela igualmente (sin restricción R4).
  - Verificar el orden de las validaciones: una reserva no-CONFIRMED debe fallar por R5 incluso si además el solicitante no está autorizado (para confirmar que R5 se comprueba antes que R7).
- Test de ReservaService.marcarReservasVencidasComoCompletadas: reservas CONFIRMED con fechaHoraFin pasada pasan a COMPLETED; las que no han vencido o no están CONFIRMED no se tocan.
- Test de integración (contexto Spring) que confirme que @EnableScheduling está activo y que ReservaScheduler invoca al método del Service (puede ser un test simple invocando el método del scheduler directamente, ya que probar el disparo real del @Scheduled con fixedRate=900000 no es práctico en un test).

## Mejoras transversales pendientes

- Implementar un @ControllerAdvice global en Exceptions/ que capture ReglaNegocioException (y las RuntimeException de "no encontrado") y las traduzca a códigos HTTP apropiados (400/404) en vez del 500 genérico actual. Afecta a BloqueoController, PistaController, UsuarioController y ReservaController por igual — debe implementarse una sola vez para las cuatro entidades, no de forma aislada.
