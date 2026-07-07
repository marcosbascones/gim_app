package com.reservas_gimnasio.proyecto.Dto.usuario;

import com.reservas_gimnasio.proyecto.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// Aqui van a ir los campos que el cliente proporciona al registrar un usuario
// el id no — ese lo genera la BD automáticamente, el cliente no lo envía al
// crear. Los campos que el cliente proporciona serían: nombre, email, password
// y rol. El estado tampoco lo envía el cliente — cuando se crea un usuario
// nuevo, el sistema lo pone ACTIVO por defecto automáticamente.
public class UsuarioRequestDTO {

    private String nombre;
    private String email;
    private String password;
    private Usuario.Rol rol;
}
