package com.reservas_gimnasio.proyecto.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_gimnasio.proyecto.models.Pista;

public interface PistaRepository extends JpaRepository<Pista, Long> {

    Optional<Pista> findByNombre(String nombre);

}
