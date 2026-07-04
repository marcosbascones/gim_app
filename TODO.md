# TODO

- Añadir test de integración con @DataJpaTest para BloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter, verificando casos de solapamiento parcial (ej. 09:00-11:00 vs 10:00-12:00). El test unitario actual usa any() en los mocks y no habría detectado el bug de orden de parámetros corregido en el commit fix: corrige orden de parámetros en query de solapamiento de Bloqueo.
