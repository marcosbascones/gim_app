package com.reservas_gimnasio.proyecto.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Reserva.ReservaResponseDTO;
import com.reservas_gimnasio.proyecto.Services.ReservaService;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(@RequestBody ReservaRequestDTO requestDTO) {
        return ResponseEntity.status(201).body(reservaService.crearReserva(requestDTO));
    }

    // usuarioSolicitanteId viaja como query param porque todavía no hay Spring
    // Security: es un parche temporal. Cuando se implemente autenticación, este
    // dato debe salir del usuario autenticado (p.ej. del token/contexto de
    // seguridad) y dejar de ser un parámetro abierto que cualquiera puede
    // manipular en la URL.
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(@PathVariable Long id,
            @RequestParam Long usuarioSolicitanteId) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id, usuarioSolicitanteId));
    }

}
