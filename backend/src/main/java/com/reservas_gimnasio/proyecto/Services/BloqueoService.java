package com.reservas_gimnasio.proyecto.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Bloqueo.BloqueoResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.BloqueoRepository;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.models.Bloqueo;

@Service
public class BloqueoService {

    private final BloqueoRepository bloqueoRepository;

    private final PistaRepository pistaRepository;

    private static final Logger logger = LoggerFactory.getLogger(BloqueoService.class);

    public BloqueoService(BloqueoRepository bloqueoRepository, PistaRepository pistaRepository) {
        this.bloqueoRepository = bloqueoRepository;
        this.pistaRepository = pistaRepository;
    }

    public BloqueoResponseDTO crearBloqueo(BloqueoRequestDTO requestDTO) {

        // Si la pista no esta vacia tira la excepcion fijate en el ! y el es empty es
        // todo la misma frase.
        if (!bloqueoRepository.findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
                pistaRepository.findById(requestDTO.getPistaId())
                        .orElseThrow(() -> new RuntimeException("Pista no encontrada")),
                requestDTO.getFechaHoraInicio(),
                requestDTO.getFechaHoraFin()).isEmpty()) {
            throw new RuntimeException("Bloqueo ya creado");
        }

        Bloqueo bloqueo = new Bloqueo();

        bloqueo.setFechaHoraFin(requestDTO.getFechaHoraFin());

        bloqueo.setFechaHoraInicio(requestDTO.getFechaHoraInicio());

        bloqueo.setMotivo(requestDTO.getMotivo());

        bloqueo.setPista(pistaRepository.findById(requestDTO.getPistaId())
                .orElseThrow(() -> new RuntimeException("Pista no encontrada")));

        Bloqueo guardado = bloqueoRepository.save(bloqueo);

        logger.info("Bloqueo creada: {}", guardado);

        return new BloqueoResponseDTO(guardado.getId(), guardado.getPista().getId(), guardado.getPista().getNombre(),
                guardado.getFechaHoraInicio(), guardado.getFechaHoraFin(), guardado.getMotivo());
    }

    public BloqueoResponseDTO obtenerBloqueoPorId(Long id) {
        Bloqueo bloqueo = bloqueoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe bloqueo con id" + id));

        logger.info("Bloqueo encontrada: {}", bloqueo);

        return new BloqueoResponseDTO(bloqueo.getId(), bloqueo.getPista().getId(), bloqueo.getPista().getNombre(),
                bloqueo.getFechaHoraInicio(), bloqueo.getFechaHoraFin(), bloqueo.getMotivo());
    }

    public List<BloqueoResponseDTO> obtenerTodosLosBloqueos() {

        List<Bloqueo> bloqueos = bloqueoRepository.findAll();

        logger.info("Obteniendo todas los bloqueos");

        return bloqueos.stream().map(p -> new BloqueoResponseDTO(p.getId(), p.getPista().getId(),
                p.getPista().getNombre(), p.getFechaHoraInicio(), p.getFechaHoraFin(), p.getMotivo()))
                .collect(Collectors.toList());
    }

    public void eliminarBloqueo(Long id) {
        Bloqueo bloqueo = bloqueoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe bloqueo con id: " + id));

        bloqueoRepository.delete(bloqueo);

        logger.info("bloqueo con id: {} eliminada", id);
    }

}
