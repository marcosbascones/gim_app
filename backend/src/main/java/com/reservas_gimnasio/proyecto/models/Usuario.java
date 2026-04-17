package com.reservas_gimnasio.proyecto.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//Generacion Getter/Setters
@Getter
@Setter
//Generación constructores con/sin argumentos
@NoArgsConstructor
@AllArgsConstructor
//Genera Strings que luego uso para poder hacer trazas
@ToString
//Le digo que esto es una entidad persistente que representa una tabla en base de datos, cada atributo sera una columna, cada objeto una fila. 
@Entity
//damos nombre a la tabla
@Table(name = "usuarios")
public class Usuario {

    public enum Rol {
        USER, ADMIN
    };

    public enum Estado {
        ACTIVO, BLOQUEADO
    };

    //Identifica el id
    @Id
    //Genera valor automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    private Estado estado;

}
