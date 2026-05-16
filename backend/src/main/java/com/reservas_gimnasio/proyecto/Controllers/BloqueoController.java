package com.reservas_gimnasio.proyecto.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoResponseDTO;
import com.reservas_gimnasio.proyecto.Services.BloqueoService;

@RestController
@RequestMapping("/bloqueos")
public class BloqueoController {

    private final BloqueoService bloqueoService;

    public BloqueoController(BloqueoService bloqueoService) {
        this.bloqueoService = bloqueoService;
    }

    @GetMapping
    public List<BloqueoResponseDTO> obtenerTodosLosBloqueos() {
        return bloqueoService.obtenerTodosLosBloqueos();
    }

    @GetMapping("/{id}")
    public BloqueoResponseDTO obtenerBloqueoPorId(@PathVariable Long id) {
        return bloqueoService.obtenerBloqueoPorId(id);
    }

    @PostMapping
    public ResponseEntity<BloqueoResponseDTO> crearBloqueo(@RequestBody BloqueoRequestDTO requestDTO) {
        return ResponseEntity.status(201).body(bloqueoService.crearBloqueo(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBloqueo(@PathVariable Long id) {
        bloqueoService.eliminarBloqueo(id);
        return ResponseEntity.noContent().build();
    }

}
