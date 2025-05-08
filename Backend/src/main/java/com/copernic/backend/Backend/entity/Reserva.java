package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatReserva;
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
    private Boolean caducada;

    @Column
    private String datareserva;

    @Column
    @Enumerated(EnumType.STRING)
    private EstatReserva estat;

    @ManyToOne
    @JoinColumn(name = "userID")
    private Usuari emailUsuari;

    @ManyToOne
    @JoinColumn(name = "idRecompensa")
    private Recompensas idRecompensa;

}
