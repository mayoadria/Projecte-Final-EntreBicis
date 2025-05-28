package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entitat que representa una reserva d'una recompensa per part d'un usuari.
 * Controla l'estat de la reserva, la seva data, si ha caducat i les relacions amb l'usuari i la recompensa corresponent.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reserva {

    /**
     * Identificador únic de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Indica si la reserva ha caducat (true) o no (false).
     */
    @Column
    private Boolean caducada;

    /**
     * Data en què es va realitzar la reserva.
     */
    @Column
    private LocalDateTime datareserva;

    /**
     * Estat actual de la reserva (RESERVADA, ASSIGNADA, RECOLLIDA, etc.).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private EstatReserva estat;

    /**
     * Usuari que ha realitzat la reserva.
     * Relació ManyToOne amb {@link Usuari}.
     */
    @ManyToOne
    @JoinColumn(name = "userID")
    private Usuari emailUsuari;

    /**
     * Recompensa associada a la reserva.
     * Relació ManyToOne amb {@link Recompensas}.
     */
    @ManyToOne
    @JoinColumn(name = "idRecompensa")
    private Recompensas idRecompensa;
}

