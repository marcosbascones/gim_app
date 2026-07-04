package com.reservas_gimnasio.proyecto.Dto.Reserva;

import java.time.LocalDateTime;

import com.reservas_gimnasio.proyecto.models.Reserva;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long pistaId;
    private String pistaNombre;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Reserva.EstadoReserva estado;
}
