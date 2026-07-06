package com.reservas_gimnasio.proyecto.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Reserva;
import com.reservas_gimnasio.proyecto.models.Usuario;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // R3 - cuenta las reservas activas futuras de un usuario (CONFIRMED con inicio posterior a "ahora").
    long countByUsuarioAndEstadoAndFechaHoraInicioAfter(
        Usuario usuario, Reserva.EstadoReserva estado, LocalDateTime ahora
    );

    // R5 (parte automática) - busca reservas CONFIRMED cuya fechaHoraFin ya haya pasado, para
    // marcarlas como COMPLETED.
    List<Reserva> findByEstadoAndFechaHoraFinBefore(Reserva.EstadoReserva estado, LocalDateTime ahora);

    // Busca reservas de la pista dada que solapan con el rango [nuevaFechaHoraInicio, nuevaFechaHoraFin).
    // OJO al orden de los parámetros: Spring Data JPA enlaza por POSICIÓN según el orden
    // de las cláusulas del nombre del método, no por el nombre que le pongas al parámetro.
    // Posición 1 -> pista
    // Posición 2 -> nuevaFechaHoraFin      (se compara con "FechaHoraInicioBefore": reserva.fechaHoraInicio < nuevaFechaHoraFin)
    // Posición 3 -> nuevaFechaHoraInicio   (se compara con "FechaHoraFinAfter": reserva.fechaHoraFin > nuevaFechaHoraInicio)
    // Este es el mismo bug de orden que se corrigió en BloqueoRepository/BloqueoService: si al
    // llamar a este método se pasan los parámetros en el orden "natural" (inicio, fin), la query
    // deja de detectar solapamientos parciales.
    List<Reserva> findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        Pista pista, LocalDateTime nuevaFechaHoraFin, LocalDateTime nuevaFechaHoraInicio
    );
}
