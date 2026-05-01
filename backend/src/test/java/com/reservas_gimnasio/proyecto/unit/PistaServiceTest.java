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

import com.reservas_gimnasio.proyecto.Dto.Pista.PistaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Pista.PistaResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.Services.PistaService;
import com.reservas_gimnasio.proyecto.models.Pista;

@ExtendWith(MockitoExtension.class)
public class PistaServiceTest {

    @Mock
    private PistaRepository pistaRepository;

    @InjectMocks
    private PistaService pistaService;

    @Test
    void crearPista() {
        PistaRequestDTO request = new PistaRequestDTO("Pista Tenis 1", Pista.Deporte.TENIS, true);

        Pista pista = new Pista();
        pista.setNombre(request.getNombre());
        pista.setDeporte(request.getDeporte());
        pista.setActiva(request.isActiva());

        when(pistaRepository.findByNombre("Pista Tenis 1")).thenReturn(Optional.empty());
        when(pistaRepository.save(any(Pista.class))).thenReturn(pista);

        PistaResponseDTO resultado = pistaService.crearPista(request);

        assertEquals("Pista Tenis 1", resultado.getNombre());
    }

    @Test
    void crearPistaExcepcionNombreDuplicado() {
        PistaRequestDTO request = new PistaRequestDTO("Pista Tenis 1", Pista.Deporte.TENIS, true);

        when(pistaRepository.findByNombre("Pista Tenis 1")).thenReturn(Optional.of(new Pista()));

        assertThrows(RuntimeException.class, () -> pistaService.crearPista(request));
    }

    @Test
    void obtenerTodasLasPistas() {
        Pista pista = new Pista();
        pista.setNombre("Pista Padel 1");
        pista.setDeporte(Pista.Deporte.PADEL);
        pista.setActiva(true);

        when(pistaRepository.findAll()).thenReturn(List.of(pista));

        List<PistaResponseDTO> resultado = pistaService.obtenerTodasLasPistas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPistaPorId() {
        Pista pista = new Pista();
        pista.setId(1L);
        pista.setNombre("Pista Futbol 1");
        pista.setDeporte(Pista.Deporte.FUTBOL);
        pista.setActiva(false);

        when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));

        PistaResponseDTO resultado = pistaService.obtenerPistaPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPistaPorIdExcepcion() {
        when(pistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pistaService.obtenerPistaPorId(99L));
    }

    @Test
    void actualizarPista() {
        Pista pista = new Pista();
        pista.setId(1L);
        pista.setNombre("Pista Tenis 1");
        pista.setDeporte(Pista.Deporte.TENIS);
        pista.setActiva(true);

        PistaRequestDTO request = new PistaRequestDTO("Pista Tenis Actualizada", Pista.Deporte.TENIS, false);

        when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));
        when(pistaRepository.save(any(Pista.class))).thenReturn(pista);

        PistaResponseDTO resultado = pistaService.actualizarPista(1L, request);

        assertEquals("Pista Tenis Actualizada", resultado.getNombre());
    }

    @Test
    void eliminarPista() {
        Pista pista = new Pista();
        pista.setId(1L);
        pista.setNombre("Pista Padel 1");

        when(pistaRepository.findById(1L)).thenReturn(Optional.of(pista));

        pistaService.eliminarPista(1L);

        verify(pistaRepository).delete(pista);
    }

    @Test
    void eliminarPistaExcepcion() {
        when(pistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pistaService.eliminarPista(99L));
    }
}