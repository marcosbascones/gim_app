package com.reservas_gimnasio.proyecto.Dto.usuario;

import com.reservas_gimnasio.proyecto.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

//Lo que el usuario recibe por ejemplo se elimina password por seguridad 
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private String nombre;
    private Usuario.Estado estado;
    private Usuario.Rol rol;


}
