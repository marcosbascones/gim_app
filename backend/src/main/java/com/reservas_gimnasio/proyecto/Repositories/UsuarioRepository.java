package com.reservas_gimnasio.proyecto.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_gimnasio.proyecto.models.Usuario;



public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    //Optional indica que puede existir o no un valor
    Optional<Usuario> findByEmail(String email);
    
}
