package com.copernic.backend.Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * Entitat que representa una posició GPS associada a una ruta.
 * Cada objecte conté la latitud, longitud i el temps en què es va registrar la posició dins la ruta corresponent.
 * Forma part de la relació de múltiples posicions GPS que defineixen el recorregut d'una {@link Rutes}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Posicio_Gps {

    /**
     * Identificador únic de la posició GPS.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Coordenada de latitud en graus decimals.
     */
    @Column
    private Double latitud;

    /**
     * Coordenada de longitud en graus decimals.
     */
    @Column
    private Double longitud;

    /**
     * Temps en segons des de l'inici de la ruta fins a aquesta posició.
     */
    @Column
    private int temps;

    /**
     * Ruta a la qual pertany aquesta posició GPS.
     * Relació ManyToOne amb {@link Rutes}.
     * S'utilitza {@code @JsonBackReference} per evitar referències circulars en la serialització.
     */
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    @JsonBackReference
    private Rutes rutes;
}

