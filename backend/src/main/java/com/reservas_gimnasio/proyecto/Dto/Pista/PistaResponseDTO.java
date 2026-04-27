package com.reservas_gimnasio.proyecto.Dto.Pista;

import com.reservas_gimnasio.proyecto.models.Pista;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PistaResponseDTO {

    private Long id;
    private String nombre;
    private Pista.Deporte deporte;
    private boolean activa;
}
