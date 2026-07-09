package com.reservas_gimnasio.proyecto;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Usuario;

//Para inyectarlo en otras clases
@Component

// Solo se carga si el perfil activo NO es "prod". Con H2 (create-drop) es
// inofensivo porque la BD se recrea vacía en cada arranque; con MySQL (datos
// persistentes) reinsertaría los mismos registros en cada reinicio y fallaría
// por la restricción UNIQUE del email.
@Profile("!prod")

// CommandLineRunner es una interfaz de Spring Boot que permite ejecutar código
// justo después de que la aplicación arranca.
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;
    private final BloqueoRepository bloqueoRepository;

    public DataLoader(UsuarioRepository usuarioRepository, PistaRepository pistaRepository, BloqueoRepository bloqueoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        usuarioRepository.save(new Usuario(null, "Carlos García",
                "carlos@gimnasio.com",
                "1234",
                Usuario.Rol.USER,
                Usuario.Estado.ACTIVO));

        usuarioRepository.save(new Usuario(null,
                "Ana López",
                "ana@gimnasio.com",
                "1234",
                Usuario.Rol.ADMIN,
                Usuario.Estado.ACTIVO));

        Pista tenis1 = pistaRepository.save(new Pista(null, "Pista Tenis 1", true, Pista.Deporte.TENIS));
        Pista tenis2 = pistaRepository.save(new Pista(null, "Pista Tenis 2", true, Pista.Deporte.TENIS));
        Pista padel1 = pistaRepository.save(new Pista(null, "Pista Padel 1", true, Pista.Deporte.PADEL));
        pistaRepository.save(new Pista(null, "Pista Futbol 1", false, Pista.Deporte.FUTBOL));

        bloqueoRepository.save(new Bloqueo(null, tenis1,
                LocalDateTime.of(2026, 5, 17, 9, 0),
                LocalDateTime.of(2026, 5, 17, 11, 0),
                "Mantenimiento de la red"));

        bloqueoRepository.save(new Bloqueo(null, tenis2,
                LocalDateTime.of(2026, 5, 18, 10, 0),
                LocalDateTime.of(2026, 5, 18, 12, 0),
                "Torneo local"));

        bloqueoRepository.save(new Bloqueo(null, padel1,
                LocalDateTime.of(2026, 5, 19, 8, 0),
                LocalDateTime.of(2026, 5, 19, 10, 0),
                "Evento privado"));
    }

}
