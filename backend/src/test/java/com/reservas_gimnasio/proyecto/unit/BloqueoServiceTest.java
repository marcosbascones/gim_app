package com.reservas_gimnasio.proyecto.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Services.BloqueoService;
import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;

@ExtendWith(MockitoExtension.class)

public class BloqueoServiceTest {

@Mock
private BloqueoRepository bloqueoRepository;

@Mock
private PistaRepository pistaRepository;

@InjectMocks
private BloqueoService bloqueoService;


@Test
void crearBloqueo() {
    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    BloqueoRequestDTO request = new BloqueoRequestDTO(
        1L,
        LocalDateTime.of(2026, 5, 17, 9, 0),
        LocalDateTime.of(2026, 5, 17, 11, 0),
        "Mantenimiento de la red"
    );

    Bloqueo bloqueo = new Bloqueo();
    bloqueo.setId(1L);
    bloqueo.setPista(pista);
    bloqueo.setFechaHoraInicio(LocalDateTime.of(2026, 5, 17, 9, 0));
    bloqueo.setFechaHoraFin(LocalDateTime.of(2026, 5, 17, 11, 0));
    bloqueo.setMotivo("Mantenimiento de la red");

    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of());
    
    when(bloqueoRepository.save(any(Bloqueo.class))).thenReturn(bloqueo);

    BloqueoResponseDTO resultado = bloqueoService.crearBloqueo(request);

    assertEquals("Mantenimiento de la red", resultado.getMotivo());
}

@Test
void crearBloqueoExcepcionSolapamiento() {
    Pista pista = new Pista();
    pista.setId(1L);
    pista.setNombre("Pista Tenis 1");

    BloqueoRequestDTO request = new BloqueoRequestDTO(
        1L,
        LocalDateTime.of(2026, 5, 17, 10, 0),
        LocalDateTime.of(2026, 5, 17, 12, 0),
        "Torneo"
    );

    Bloqueo bloqueoExistente = new Bloqueo();
    bloqueoExistente.setId(1L);
    bloqueoExistente.setPista(pista);
    bloqueoExistente.setFechaHoraInicio(LocalDateTime.of(2026, 5, 17, 9, 0));
    bloqueoExistente.setFechaHoraFin(LocalDateTime.of(2026, 5, 17, 11, 0));
    bloqueoExistente.setMotivo("Mantenimiento de la red");

    when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
    when(bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        any(), any(), any()
    )).thenReturn(List.of(bloqueoExistente));

    assertThrows(RuntimeException.class, () -> bloqueoService.crearBloqueo(request));
}




}
