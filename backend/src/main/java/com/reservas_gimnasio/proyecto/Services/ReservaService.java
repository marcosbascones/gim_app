package com.reservas_gimnasio.proyecto.Services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaResponseDTO;
import com.reservas_gimnasio.proyecto.Exceptions.ReglaNegocioException;
import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.ReservaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Reserva;
import com.reservas_gimnasio.proyecto.models.Usuario;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private final UsuarioRepository usuarioRepository;

    private final PistaRepository pistaRepository;

    private final BloqueoRepository bloqueoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
            PistaRepository pistaRepository, BloqueoRepository bloqueoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    public ReservaResponseDTO crearReserva(ReservaRequestDTO requestDTO) {

        // La reserva debe empezar en el futuro: no se admite el instante actual ni el pasado.
        if (!requestDTO.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora de inicio debe ser posterior al momento actual");
        }

        // R6 - la reserva debe durar entre 1 y 2 horas, ambos inclusive.
        Duration duracion = Duration.between(requestDTO.getFechaHoraInicio(), requestDTO.getFechaHoraFin());

        if (duracion.toMinutes() < 60 || duracion.toMinutes() > 120) {
            throw new ReglaNegocioException("La duración de la reserva debe estar entre 1 y 2 horas");
        }

        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pista pista = pistaRepository.findById(requestDTO.getPistaId())
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));

        // R1 - no puede solaparse con otra reserva de la misma pista que no esté cancelada.
        List<Reserva> reservasSolapadas = reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, requestDTO.getFechaHoraFin(), requestDTO.getFechaHoraInicio());

        boolean hayReservaSolapada = reservasSolapadas.stream()
                .anyMatch(r -> r.getEstado() != Reserva.EstadoReserva.CANCELLED);

        if (hayReservaSolapada) {
            throw new ReglaNegocioException("Ya existe una reserva para esa pista en ese horario");
        }

        // R2 - la pista no debe estar bloqueada (mantenimiento, eventos) en ese horario.
        List<Bloqueo> bloqueosSolapados = bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, requestDTO.getFechaHoraFin(), requestDTO.getFechaHoraInicio());

        if (!bloqueosSolapados.isEmpty()) {
            throw new ReglaNegocioException("La pista está bloqueada en ese horario");
        }

        Reserva reserva = new Reserva();

        reserva.setUsuario(usuario);
        reserva.setPista(pista);
        reserva.setFechaHoraInicio(requestDTO.getFechaHoraInicio());
        reserva.setFechaHoraFin(requestDTO.getFechaHoraFin());
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMED);

        Reserva guardada = reservaRepository.save(reserva);

        logger.info("Reserva creada: {}", guardada);

        return new ReservaResponseDTO(guardada.getId(), guardada.getUsuario().getId(),
                guardada.getUsuario().getNombre(), guardada.getPista().getId(), guardada.getPista().getNombre(),
                guardada.getFechaHoraInicio(), guardada.getFechaHoraFin(), guardada.getEstado());
    }

}
