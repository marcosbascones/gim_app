package com.reservas_gimnasio.proyecto.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Table(name = "bloqueos")
public class Bloqueo {

    // Identifica el id
    @Id
    // Genera valor automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="pista_id")
    private Pista pista;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;


    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false)
    private String motivo;

}
