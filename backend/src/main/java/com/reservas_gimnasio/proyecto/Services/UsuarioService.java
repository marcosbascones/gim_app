package com.reservas_gimnasio.proyecto.Services;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        // Stream- Comienza el procesamiento, convierte una colleción en un flujo de
        // datos que se puede procesar
        // Map- Transforma cada elemento de la lista en lo que se indique en este
        // ejemplo se dice que cada usuario va a transformarse en UsuarioResponseDTO
        // collect(Collectors.toList())-Recoge el resultado y lo vuelve a transformar en
        // una lista
        logger.info("Obteniendo todos los usuarios");
        return usuarios.stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getEmail(), u.getNombre(), u.getEstado(), u.getRol()))
                .collect(Collectors.toList());

    }

    // Con el id buscamos el usuario que queremos actualizarUsuario
    // Con request traemos los nuevos datos que el usuaio ha introducido y que vamos
    // a utilizar para actualizar la info

    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + id));

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());

        Usuario guardado = usuarioRepository.save(usuario);

        logger.info("Usuario actualizado: {}", guardado);
        return new UsuarioResponseDTO(guardado.getId(), guardado.getEmail(), guardado.getNombre(), guardado.getEstado(),
                guardado.getRol());

    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id: " + id));

        logger.info("Usuario con id: {} eliminado", id);

        usuarioRepository.delete(usuario);

    }

}
