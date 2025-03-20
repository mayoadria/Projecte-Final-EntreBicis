package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.Estat;
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
public class Rutes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nom;
    @Column
    private String descripcio;
    @Column
    private int km;
    @Enumerated(EnumType.STRING)
    private Estat estat;
    @Column
    private Double velMedia;
    @Column
    private Double velMax;
    @Column
    private Double velMitjaKM;
    @Column
    private Double tempsParat;
    @Column
    private String temps;

    @ManyToOne
    @JoinColumn(name = "usuari_email", nullable = false)
    private Usuari usuari;

    @OneToMany(mappedBy = "rutes", cascade = CascadeType.ALL)
    private List<Posicio_Gps> posicionsGps;


}
