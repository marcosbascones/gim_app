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

		// La reserva debe empezar en el futuro: no se admite el instante actual ni el
		// pasado.
		if (!requestDTO.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
			throw new ReglaNegocioException(
					"La fecha y hora de inicio debe ser posterior al momento actual");
		}

		// R6 - la reserva debe durar entre 1 y 2 horas, ambos inclusive.
		Duration duracion = Duration.between(requestDTO.getFechaHoraInicio(), requestDTO.getFechaHoraFin());

		if (duracion.toMinutes() < 60 || duracion.toMinutes() > 120) {
			throw new ReglaNegocioException("La duración de la reserva debe estar entre 1 y 2 horas");
		}

		Usuario usuario = usuarioRepository.findById(requestDTO.getUsuarioId())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// R3 - máximo 3 reservas activas futuras (CONFIRMED con inicio en el futuro) por usuario.
		long reservasActivasFuturas = reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
				usuario, Reserva.EstadoReserva.CONFIRMED, LocalDateTime.now());

		if (reservasActivasFuturas >= 3) {
			throw new ReglaNegocioException("El usuario ya tiene el máximo de 3 reservas activas futuras");
		}

		Pista pista = pistaRepository.findById(requestDTO.getPistaId())
				.orElseThrow(() -> new RuntimeException("Pista no encontrada"));

		// R1 - no puede solaparse con otra reserva de la misma pista que no esté
		// cancelada.
		List<Reserva> reservasSolapadas = reservaRepository
				.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
						pista, requestDTO.getFechaHoraFin(), requestDTO.getFechaHoraInicio());

		boolean hayReservaSolapada = reservasSolapadas.stream()
				.anyMatch(r -> r.getEstado() != Reserva.EstadoReserva.CANCELLED);

		if (hayReservaSolapada) {
			throw new ReglaNegocioException("Ya existe una reserva para esa pista en ese horario");
		}

		// R2 - la pista no debe estar bloqueada (mantenimiento, eventos) en ese
		// horario.
		List<Bloqueo> bloqueosSolapados = bloqueoRepository
				.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
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
				guardada.getUsuario().getNombre(), guardada.getPista().getId(),
				guardada.getPista().getNombre(),
				guardada.getFechaHoraInicio(), guardada.getFechaHoraFin(), guardada.getEstado());
	}

	public ReservaResponseDTO cancelarReserva(Long reservaId, Long usuarioSolicitanteId) {

		Reserva reserva = reservaRepository.findById(reservaId)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

		// R5 - solo se pueden cancelar reservas CONFIRMED. No depende de quién pide la
		// cancelación, así que se comprueba primero y sin necesidad de cargar el usuario.
		if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMED) {
			throw new ReglaNegocioException("Solo se pueden cancelar reservas confirmadas");
		}

		Usuario usuarioSolicitante = usuarioRepository.findById(usuarioSolicitanteId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		boolean esDueno = reserva.getUsuario().getId().equals(usuarioSolicitante.getId());
		boolean esAdmin = usuarioSolicitante.getRol() == Usuario.Rol.ADMIN;

		// R7 - solo el dueño de la reserva o un ADMIN pueden cancelarla. Es una
		// comprobación de autorización: debe ir antes que R4 para no evaluar una regla
		// de negocio (el margen de 2h) sobre alguien que ni siquiera tiene permiso.
		if (!esDueno && !esAdmin) {
			throw new ReglaNegocioException("No autorizado para cancelar esta reserva");
		}

		// R4 - un usuario normal solo puede cancelar con más de 2h de antelación; los
		// admins quedan exentos de esta restricción.
		if (!esAdmin) {
			Duration margen = Duration.between(LocalDateTime.now(), reserva.getFechaHoraInicio());

			if (margen.toMinutes() < 120) {
				throw new ReglaNegocioException(
						"Solo se puede cancelar una reserva con más de 2 horas de antelación");
			}
		}

		reserva.setEstado(Reserva.EstadoReserva.CANCELLED);

		Reserva guardada = reservaRepository.save(reserva);

		logger.info("Reserva cancelada: {}", guardada);

		return new ReservaResponseDTO(guardada.getId(), guardada.getUsuario().getId(),
				guardada.getUsuario().getNombre(), guardada.getPista().getId(),
				guardada.getPista().getNombre(),
				guardada.getFechaHoraInicio(), guardada.getFechaHoraFin(), guardada.getEstado());
	}

	public void marcarReservasVencidasComoCompletadas() {

		List<Reserva> reservasVencidas = reservaRepository.findByEstadoAndFechaHoraFinBefore(
				Reserva.EstadoReserva.CONFIRMED, LocalDateTime.now());

		reservasVencidas.forEach(reserva -> reserva.setEstado(Reserva.EstadoReserva.COMPLETED));

		reservaRepository.saveAll(reservasVencidas);

		logger.info("Reservas marcadas como COMPLETED: {}", reservasVencidas.size());
	}

}
