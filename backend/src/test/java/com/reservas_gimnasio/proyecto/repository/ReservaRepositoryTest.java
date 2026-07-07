package com.reservas_gimnasio.proyecto.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.ReservaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Reserva;
import com.reservas_gimnasio.proyecto.models.Usuario;

@DataJpaTest
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PistaRepository pistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void encuentraReservaConSolapamientoParcial() {
        Pista pista = crearYGuardarPista();
        crearYGuardarReserva(crearYGuardarUsuario("ana@test.com"), pista,
                LocalDateTime.of(2026, 7, 10, 9, 0), LocalDateTime.of(2026, 7, 10, 11, 0),
                Reserva.EstadoReserva.CONFIRMED);

        // Caso con solapamiento parcial real: la reserva existente es 09:00-11:00 y
        // aquí se consulta 10:00-12:00, que se solapa entre 10:00 y 11:00.
        // Igual que con BloqueoRepository, un test unitario con mocks (any()) no
        // detectaría un error en el orden de los parámetros de esta query, porque
        // ahí se simula la respuesta en vez de ejecutar el SQL real.
        List<Reserva> resultado = reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, LocalDateTime.of(2026, 7, 10, 12, 0), LocalDateTime.of(2026, 7, 10, 10, 0));

        assertFalse(resultado.isEmpty());
    }

    @Test
    void noEncuentraReservaSinSolapamiento() {
        Pista pista = crearYGuardarPista();
        crearYGuardarReserva(crearYGuardarUsuario("ana@test.com"), pista,
                LocalDateTime.of(2026, 7, 10, 9, 0), LocalDateTime.of(2026, 7, 10, 11, 0),
                Reserva.EstadoReserva.CONFIRMED);

        // Caso sin solapamiento: se consulta 11:00-12:00, justo después de que
        // termine la reserva existente (09:00-11:00).
        List<Reserva> resultado = reservaRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, LocalDateTime.of(2026, 7, 10, 12, 0), LocalDateTime.of(2026, 7, 10, 11, 0));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void cuentaSoloReservasConfirmedFuturasDelUsuario() {
        Usuario usuario = crearYGuardarUsuario("ana@test.com");
        Pista pista = crearYGuardarPista();

        LocalDateTime ahora = LocalDateTime.now();

        // Cuentan: dos CONFIRMED futuras.
        crearYGuardarReserva(usuario, pista, ahora.plusDays(1), ahora.plusDays(1).plusHours(1),
                Reserva.EstadoReserva.CONFIRMED);
        crearYGuardarReserva(usuario, pista, ahora.plusDays(2), ahora.plusDays(2).plusHours(1),
                Reserva.EstadoReserva.CONFIRMED);

        // No cuentan: CANCELLED futura, COMPLETED futura y CONFIRMED ya pasada.
        crearYGuardarReserva(usuario, pista, ahora.plusDays(3), ahora.plusDays(3).plusHours(1),
                Reserva.EstadoReserva.CANCELLED);
        crearYGuardarReserva(usuario, pista, ahora.plusDays(4), ahora.plusDays(4).plusHours(1),
                Reserva.EstadoReserva.COMPLETED);
        crearYGuardarReserva(usuario, pista, ahora.minusDays(1), ahora.minusDays(1).plusHours(1),
                Reserva.EstadoReserva.CONFIRMED);

        long total = reservaRepository.countByUsuarioAndEstadoAndFechaHoraInicioAfter(
                usuario, Reserva.EstadoReserva.CONFIRMED, ahora);

        assertEquals(2, total);
    }

    @Test
    void encuentraSoloReservasConfirmedConFechaFinPasada() {
        Usuario usuario = crearYGuardarUsuario("ana@test.com");
        Pista pista = crearYGuardarPista();

        LocalDateTime ahora = LocalDateTime.now();

        // Debe encontrarla: CONFIRMED con fechaHoraFin ya pasada.
        crearYGuardarReserva(usuario, pista, ahora.minusHours(3), ahora.minusHours(1),
                Reserva.EstadoReserva.CONFIRMED);

        // No debe encontrarlas: CONFIRMED aún no vencida y CANCELLED con fin pasado.
        crearYGuardarReserva(usuario, pista, ahora.plusHours(1), ahora.plusHours(2),
                Reserva.EstadoReserva.CONFIRMED);
        crearYGuardarReserva(usuario, pista, ahora.minusHours(3), ahora.minusHours(1),
                Reserva.EstadoReserva.CANCELLED);

        List<Reserva> resultado = reservaRepository.findByEstadoAndFechaHoraFinBefore(
                Reserva.EstadoReserva.CONFIRMED, ahora);

        assertEquals(1, resultado.size());
        assertEquals(Reserva.EstadoReserva.CONFIRMED, resultado.get(0).getEstado());
        assertTrue(resultado.get(0).getFechaHoraFin().isBefore(ahora));
    }

    private Pista crearYGuardarPista() {
        Pista pista = new Pista();
        pista.setNombre("Pista Test " + System.nanoTime());
        pista.setDeporte(Pista.Deporte.PADEL);
        pista.setActiva(true);
        return pistaRepository.save(pista);
    }

    private Usuario crearYGuardarUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Test");
        usuario.setEmail(email + System.nanoTime());
        usuario.setPassword("password");
        usuario.setRol(Usuario.Rol.USER);
        usuario.setEstado(Usuario.Estado.ACTIVO);
        return usuarioRepository.save(usuario);
    }

    private void crearYGuardarReserva(Usuario usuario, Pista pista, LocalDateTime inicio, LocalDateTime fin,
            Reserva.EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setPista(pista);
        reserva.setFechaHoraInicio(inicio);
        reserva.setFechaHoraFin(fin);
        reserva.setEstado(estado);
        reservaRepository.save(reserva);
    }
}
