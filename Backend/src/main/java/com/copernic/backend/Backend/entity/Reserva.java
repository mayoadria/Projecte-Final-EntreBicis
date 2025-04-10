package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatReserva;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String datareserva;

    @Column
    @Enumerated(EnumType.STRING)
    private EstatReserva estat;

    @ManyToOne
    @JoinColumn(name = "userID")
    @JsonBackReference
    private Usuari emailUsuari;

    @OneToOne
    @JoinColumn(name = "idRecompensa",unique = true)
    private Recompensas idRecompensa;

}
