package com.reservas_gimnasio.proyecto.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas_gimnasio.proyecto.Dto.Usuario.UsuarioRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Usuario.UsuarioResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.UsuarioRepository;
import com.reservas_gimnasio.proyecto.Services.UsuarioService;
import com.reservas_gimnasio.proyecto.models.Usuario;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearUsuario() {

        UsuarioRequestDTO request = new UsuarioRequestDTO("Juan", "juan@test.com", "1234", Usuario.Rol.USER);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());
        usuario.setEstado(Usuario.Estado.ACTIVO);

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.crearUsuario(request);

        assertEquals("juan@test.com", resultado.getEmail());

    }

    @Test
    void crearUsuarioExcepcion() {

        UsuarioRequestDTO request = new UsuarioRequestDTO("Juan", "juan@test.com", "1234", Usuario.Rol.USER);

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(RuntimeException.class, () -> usuarioService.crearUsuario(request));

    }

    @Test
    void obtenerTodosLosUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");

        List<Usuario> lista = List.of(usuario);

        when(usuarioRepository.findAll()).thenReturn(lista);

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertEquals(1, resultado.size());

    }

    @Test
    void obtenerUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertEquals(1L, resultado.getId());

    }

    @Test
    void actualizarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");
        usuario.setRol(Usuario.Rol.USER);
        usuario.setEstado(Usuario.Estado.ACTIVO);

        UsuarioRequestDTO request = new UsuarioRequestDTO("JuanActualizado", "juannuevo@test.com", "5678",
                Usuario.Rol.USER);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.actualizarUsuario(1L, request);

        assertEquals("juannuevo@test.com", resultado.getEmail());
    }

    @Test
    void eliminarUsuario() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).delete(usuario);

    }

}
