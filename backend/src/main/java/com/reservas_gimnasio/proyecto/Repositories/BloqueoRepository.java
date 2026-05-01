package com.reservas_gimnasio.proyecto.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_gimnasio.proyecto.models.Bloqueo;
import com.reservas_gimnasio.proyecto.models.Pista;

public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {

    //Busca si hay algun bloqueo entre las franjas de horario y con el id de la pista facilitado 
    List<Bloqueo> findByPistaAndFechaHoraInicioBeforeAndFechaHoraFinAfter(
        Pista pista, LocalDateTime fechaHoraFin, LocalDateTime fechaHoraInicio
    );
}