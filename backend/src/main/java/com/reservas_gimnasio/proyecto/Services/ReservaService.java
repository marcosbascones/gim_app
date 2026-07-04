package com.reservas_gimnasio.proyecto.Services;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaResponseDTO;
import com.reservas_gimnasio.proyecto.Exceptions.ReglaNegocioException;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.ReservaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Reserva;
import com.reservas_gimnasio.proyecto.models.Usuario;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private final UsuarioRepository usuarioRepository;

    private final PistaRepository pistaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
            PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
    }

    public ReservaResponseDTO crearReserva(ReservaRequestDTO requestDTO) {

        // R6 - la reserva debe durar entre 1 y 2 horas, ambos inclusive.
        Duration duracion = Duration.between(requestDTO.getFechaHoraInicio(), requestDTO.getFechaHoraFin());

        if (duracion.toMinutes() < 60 || duracion.toMinutes() > 120) {
            throw new ReglaNegocioException("La duración de la reserva debe estar entre 1 y 2 horas");
        }

        Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pista pista = pistaRepository.findById(requestDTO.getPistaId())
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));

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
