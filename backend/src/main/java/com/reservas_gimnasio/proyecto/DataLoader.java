package com.reservas_gimnasio.proyecto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Usuario;

//Para inyectarlo en otras clases 
@Component

// CommandLineRunner es una interfaz de Spring Boot que permite ejecutar código
// justo después de que la aplicación arranca.
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    }

}
