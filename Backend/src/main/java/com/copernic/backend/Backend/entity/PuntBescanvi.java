package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @OneToMany(mappedBy = "puntBescanviId", cascade = CascadeType.ALL)
    private List<Recompensas> puntBescanviID;
}
