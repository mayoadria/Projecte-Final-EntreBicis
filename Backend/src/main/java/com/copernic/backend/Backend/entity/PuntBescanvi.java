package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
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

    @Lob
    private String foto;

    @OneToMany(mappedBy = "puntBescanviId", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Recompensas> puntBescanviID;
}
