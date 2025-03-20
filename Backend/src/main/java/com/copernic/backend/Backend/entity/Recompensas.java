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
public class Recompensas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String descripcio;
    @Column
    private int cost;
    @Column
    private String observacions;
    @Enumerated(EnumType.STRING)
    private Estat estat;

    @ManyToOne
    @JoinColumn(name = "usuari_email", nullable = false)
    private Usuari usuariRecompensa;
    @OneToMany(mappedBy = "recompensa", cascade = CascadeType.ALL)
    private List<PuntBescanvi> puntsBescanvi;
}
