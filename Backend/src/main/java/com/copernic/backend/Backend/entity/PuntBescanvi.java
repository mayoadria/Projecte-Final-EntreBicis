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
public class PuntBescanvi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nom;
    @Column
    private String codiPostal;
    @Column
    private String adreca;
    @Column
    private String telefon;
    @Column
    private String personaContacte;
    @Column
    private String email;
    @Column
    private String observacions;

    @ManyToOne
    @JoinColumn(name = "recompensa_id", nullable = false)
    private Recompensas recompensa;
}
