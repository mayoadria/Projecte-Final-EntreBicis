package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.entity.enums.CicloRuta;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @Column(name = "km", columnDefinition = "DECIMAL(7,3)") // 4 enteros + 3 decimales de precisión
    private double km;

    @Enumerated(EnumType.STRING)
    private EstatRutes estat;         // VALIDA / INVALIDA

    @Enumerated(EnumType.STRING)
    private CicloRuta cicloRuta;      // INICIADA / PAUSADA / FINALITZADA

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

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuari_email", nullable = false)
    @JsonBackReference
    private Usuari usuari;

    @OneToMany(mappedBy = "rutes", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Posicio_Gps> posicionsGps;
}
