package com.copernic.backend.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Entitat que representa un punt de bescanvi dins del sistema.
 * Un punt de bescanvi és un lloc físic on els usuaris poden recollir recompenses.
 * Conté informació de contacte, localització i les recompenses associades.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class PuntBescanvi {

    /**
     * Identificador únic del punt de bescanvi.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom del punt de bescanvi.
     */
    @Column
    private String nom;

    /**
     * Codi postal on es troba el punt de bescanvi.
     */
    @Column
    private String codiPostal;

    /**
     * Adreça física del punt de bescanvi.
     */
    @Column
    private String adreca;

    /**
     * Telèfon de contacte del punt de bescanvi.
     */
    @Column
    private String telefon;

    /**
     * Nom de la persona de contacte responsable del punt.
     */
    @Column
    private String personaContacte;

    /**
     * Correu electrònic de contacte del punt de bescanvi.
     */
    @Column
    private String email;

    /**
     * Observacions o comentaris addicionals sobre el punt de bescanvi.
     */
    @Column
    private String observacions;

    /**
     * Imatge representativa del punt de bescanvi, emmagatzemada com a cadena en Base64.
     */
    @Lob
    private String foto;

    /**
     * Llista de recompenses associades a aquest punt de bescanvi.
     * Relació OneToMany amb la classe {@link Recompensas}.
     * S'exclou del {@code toString} i de la serialització JSON per evitar referències circulars i sobrecàrrega.
     */
    @OneToMany(mappedBy = "puntBescanviId")
    @ToString.Exclude
    @JsonIgnore
    private List<Recompensas> puntBescanviID;
}

