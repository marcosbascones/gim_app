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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "pistas")

public class Pista {

    @Id
    // Genera valor automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Deporte {
        TENIS, PADEL, FUTBOL
    };

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean activa;

    @Enumerated(EnumType.STRING)
    private Deporte deporte;

}
