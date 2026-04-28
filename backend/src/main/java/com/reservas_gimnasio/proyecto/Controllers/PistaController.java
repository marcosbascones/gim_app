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

import com.reservas_gimnasio.proyecto.Dto.Pista.PistaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Pista.PistaResponseDTO;
import com.reservas_gimnasio.proyecto.Services.PistaService;

@RestController
@RequestMapping("/pistas")

public class PistaController {

    private final PistaService pistaService;

    public PistaController(PistaService pistaService) {
        this.pistaService = pistaService;
    }

    @GetMapping
    public List<PistaResponseDTO> obtenerTodasLasPistas() {
        return pistaService.obtenerTodasLasPistas();
    }

    @GetMapping("/{id}")
    public PistaResponseDTO obtenerPistaPorId(@PathVariable Long id) {
        return pistaService.obtenerPistaPorId(id);
    }

    @PostMapping
    public PistaResponseDTO crearPista(@RequestBody PistaRequestDTO request) {
        return pistaService.crearPista(request);
    }

    @PutMapping("/{id}")
    public PistaResponseDTO actualizarPista(@PathVariable Long id, @RequestBody PistaRequestDTO request) {
        return pistaService.actualizarPista(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminarPista(@PathVariable Long id) {
        pistaService.eliminarPista(id);
    }
}
