package com.reservas_gimnasio.proyecto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Pista;
import com.reservas_gimnasio.proyecto.models.Usuario;

//Para inyectarlo en otras clases 
@Component

// CommandLineRunner es una interfaz de Spring Boot que permite ejecutar código
// justo después de que la aplicación arranca.
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;

    public DataLoader(UsuarioRepository usuarioRepository, PistaRepository pistaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
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

        pistaRepository.save(new Pista(null, "Pista Tenis 1", true, Pista.Deporte.TENIS));
        pistaRepository.save(new Pista(null, "Pista Tenis 2", true, Pista.Deporte.TENIS));
        pistaRepository.save(new Pista(null, "Pista Padel 1", true, Pista.Deporte.PADEL));
        pistaRepository.save(new Pista(null, "Pista Futbol 1", false, Pista.Deporte.FUTBOL));

    }

}
