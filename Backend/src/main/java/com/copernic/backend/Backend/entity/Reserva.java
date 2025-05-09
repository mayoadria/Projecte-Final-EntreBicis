package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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
    private LocalDateTime datareserva;

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
