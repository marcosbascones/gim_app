package com.reservas_gimnasio.proyecto.Dto.Reserva;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    private Long usuarioId;
    private Long pistaId;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
}
