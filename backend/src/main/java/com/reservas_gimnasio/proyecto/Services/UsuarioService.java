package com.reservas_gimnasio.proyecto.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas_gimnasio.proyecto.Dto.usuario.UsuarioRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.usuario.UsuarioResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.models.Usuario;

@Service
// Si todo va bien ok si no hace rollback
@Transactional

public class UsuarioService {

    // Inyectamos la dependencia de repository y luego se la metemos al constructor
    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());
        usuario.setEstado(Usuario.Estado.ACTIVO);

        Usuario guardado = usuarioRepository.save(usuario);

        logger.info("Usuario creado: {}", guardado);
        return new UsuarioResponseDTO(guardado.getId(), guardado.getEmail(), guardado.getNombre(), guardado.getEstado(),
                guardado.getRol());
    }

    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + id));

        logger.info("Usuario encontrado: {}", usuario);
        return new UsuarioResponseDTO(usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getEstado(),
                usuario.getRol());

    }

    

}
