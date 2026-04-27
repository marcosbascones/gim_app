package com.reservas_gimnasio.proyecto.Controllers;

import java.util.List;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas_gimnasio.proyecto.Dto.Usuario.UsuarioRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Usuario.UsuarioResponseDTO;
import com.reservas_gimnasio.proyecto.Services.UsuarioService;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {

    private final UsuarioService usuarioService;
    

    public UsuarioController(UsuarioService usuariosService) {
        this.usuarioService = usuariosService;
    }

    @GetMapping
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @GetMapping("/{id}")

    public UsuarioResponseDTO obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @PostMapping

    public UsuarioResponseDTO crearUsuario(@RequestBody UsuarioRequestDTO request) {

        return usuarioService.crearUsuario(request);
    }

    @PutMapping("/{id}")

    public UsuarioResponseDTO actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO request) {

        return usuarioService.actualizarUsuario(id, request);
    }

    @DeleteMapping("/{id}")

    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }

}
