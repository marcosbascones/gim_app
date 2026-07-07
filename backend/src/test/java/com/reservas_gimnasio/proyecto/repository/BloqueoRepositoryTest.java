package com.reservas_gimnasio.proyecto.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;

@DataJpaTest
public class BloqueoRepositoryTest {

    @Autowired
    private BloqueoRepository bloqueoRepository;

    @Autowired
    private PistaRepository pistaRepository;

    @Test
    void encuentraBloqueoConSolapamientoParcial() {
        Pista pista = crearYGuardarPista();
        crearYGuardarBloqueo(pista, LocalDateTime.of(2026, 7, 10, 9, 0), LocalDateTime.of(2026, 7, 10, 11, 0));

        // Caso con solapamiento parcial real: el bloqueo existente es 09:00-11:00 y
        // aquí se consulta 10:00-12:00, que se solapa entre 10:00 y 11:00.
        // Esto es justo lo que el test unitario con mocks (any()) no podía detectar,
        // porque ahí se simulaba la respuesta del repositorio en vez de ejecutar la
        // query real contra la base de datos.
        List<Bloqueo> resultado = bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, LocalDateTime.of(2026, 7, 10, 12, 0), LocalDateTime.of(2026, 7, 10, 10, 0));

        assertFalse(resultado.isEmpty());
    }

    @Test
    void noEncuentraBloqueoSinSolapamiento() {
        Pista pista = crearYGuardarPista();
        crearYGuardarBloqueo(pista, LocalDateTime.of(2026, 7, 10, 9, 0), LocalDateTime.of(2026, 7, 10, 11, 0));

        // Caso sin solapamiento: se consulta 11:00-12:00, justo después de que
        // termine el bloqueo existente (09:00-11:00).
        List<Bloqueo> resultado = bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pista, LocalDateTime.of(2026, 7, 10, 12, 0), LocalDateTime.of(2026, 7, 10, 11, 0));

        assertTrue(resultado.isEmpty());
    }

    private Pista crearYGuardarPista() {
        Pista pista = new Pista();
        pista.setNombre("Pista Test");
        pista.setDeporte(Pista.Deporte.PADEL);
        pista.setActiva(true);
        return pistaRepository.save(pista);
    }

    private void crearYGuardarBloqueo(Pista pista, LocalDateTime inicio, LocalDateTime fin) {
        Bloqueo bloqueo = new Bloqueo();
        bloqueo.setPista(pista);
        bloqueo.setFechaHoraInicio(inicio);
        bloqueo.setFechaHoraFin(fin);
        bloqueo.setMotivo("Mantenimiento");
        bloqueoRepository.save(bloqueo);
    }
}
