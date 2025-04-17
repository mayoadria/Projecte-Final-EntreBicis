package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.Estat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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
    @Lob
    private String foto;
    @Column
    private String dataCreacio;
    @Column
    private LocalDateTime DataAsignacio;

    @ManyToOne
    @JoinColumn(name = "usuari_email", nullable = true)
    @JsonBackReference
    private Usuari usuariRecompensa;

    @ManyToOne
    @JoinColumn(name = "puntBescanviID", nullable = true)
    @JsonIgnoreProperties({"puntBescanviID"})
    private PuntBescanvi puntBescanviId;

    @OneToMany(mappedBy = "idRecompensa")
    @JsonIgnore
    private List<Reserva> reservas;
}
