package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Posicio_Gps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Column
    private int temps;

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    private Rutes rutes;
}
