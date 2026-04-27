package com.reservas_gimnasio.proyecto.Services;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reservas_gimnasio.proyecto.Dto.Pista.PistaRequestDTO;
import com.reservas_gimnasio.proyecto.Dto.Pista.PistaResponseDTO;
import com.reservas_gimnasio.proyecto.Repositories.PistaRepository;
import com.reservas_gimnasio.proyecto.models.Pista;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class PistaService {

    private final PistaRepository pistaRepository;

    private static final Logger logger = LoggerFactory.getLogger(PistaService.class);

    public PistaService(PistaRepository pistaRepository) {
        this.pistaRepository = pistaRepository;
    }

    public PistaResponseDTO crearPista(PistaRequestDTO request) {

        if (pistaRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new RuntimeException("Nombre ya utilizado");
        }

        Pista pista = new Pista();

        pista.setNombre(request.getNombre());
        pista.setActiva(request.isActiva());
        pista.setDeporte(request.getDeporte());

        Pista guardado = pistaRepository.save(pista);

        logger.info("Pista creada: {}", guardado);

        return new PistaResponseDTO(guardado.getId(), guardado.getNombre(), guardado.getDeporte(), guardado.isActiva());

    }

    public PistaResponseDTO obtenerPistaPorId(Long id) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe pista con id" + id));

        logger.info("Pista encontrada: {}", pista);

        return new PistaResponseDTO(pista.getId(), pista.getNombre(), pista.getDeporte(), pista.isActiva());

    }

    public List<PistaResponseDTO> obtenerTodasLasPistas() {

        List<Pista> pistas = pistaRepository.findAll();

        logger.info("Obteniendo todas los usuarios");

        return pistas.stream()
                .map(p -> new PistaResponseDTO(p.getId(), p.getNombre(), p.getDeporte(), p.isActiva()))
                .collect(Collectors.toList());
    }

    public PistaResponseDTO actualizarPista(Long id, PistaRequestDTO request) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe pista con id: " + id));

        pista.setNombre(request.getNombre());
        pista.setDeporte(request.getDeporte());
        pista.setActiva(request.isActiva());

        Pista guardado = pistaRepository.save(pista);

        logger.info("Pista actualizada: {}", guardado);
        return new PistaResponseDTO(guardado.getId(), guardado.getNombre(), guardado.getDeporte(), guardado.isActiva());
    }

    public void eliminarPista(Long id) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe pista con id: " + id));

        logger.info("Pista con id: {} eliminada", id);
        pistaRepository.delete(pista);
    }

}
