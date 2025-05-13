package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.Estat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entitat que representa una recompensa dins del sistema.
 * Conté la informació relativa a la seva descripció, cost, estat i dates importants,
 * així com les relacions amb usuaris, punts de bescanvi i reserves.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Recompensas {

    /**
     * Identificador únic de la recompensa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Descripció de la recompensa.
     */
    @Column
    private String descripcio;

    /**
     * Cost en punts per obtenir la recompensa.
     */
    @Column
    private int cost;

    /**
     * Observacions addicionals sobre la recompensa.
     */
    @Column
    private String observacions;

    /**
     * Estat actual de la recompensa (DISPONIBLES, RESERVADES, ASSIGNADES, etc.).
     */
    @Enumerated(EnumType.STRING)
    private Estat estat;

    /**
     * Imatge de la recompensa en format Base64.
     */
    @Lob
    private String foto;

    /**
     * Data de creació de la recompensa.
     */
    @Column
    private LocalDateTime dataCreacio;

    /**
     * Data en què la recompensa ha estat assignada a un usuari.
     */
    @Column
    private LocalDateTime dataAsignacio;

    /**
     * Data de reserva de la recompensa.
     */
    @Column
    private LocalDateTime dataReserva;

    /**
     * Data d'entrega de la recompensa a l'usuari.
     */
    @Column
    private LocalDateTime dataEntrega;

    /**
     * Usuari al qual s'ha assignat la recompensa.
     * Relació ManyToOne amb {@link Usuari}.
     * S'utilitza {@code @JsonBackReference} per evitar referències circulars en la serialització.
     */
    @ManyToOne
    @JoinColumn(name = "usuari_email", nullable = true)
    @JsonBackReference
    private Usuari usuariRecompensa;

    /**
     * Punt de bescanvi associat a la recompensa.
     * Relació ManyToOne amb {@link PuntBescanvi}.
     */
    @ManyToOne
    @JoinColumn(name = "puntBescanviID", nullable = true)
    @JsonIgnoreProperties({"puntBescanviID"})
    private PuntBescanvi puntBescanviId;

    /**
     * Llista de reserves associades a aquesta recompensa.
     * Relació OneToMany amb {@link Reserva}.
     * S'ignoren propietats per evitar bucles infinits en la serialització.
     */
    @OneToMany(mappedBy = "idRecompensa")
    @JsonIgnoreProperties({"emailUsuari","idRecompensa"})
    private List<Reserva> reservas;
}

