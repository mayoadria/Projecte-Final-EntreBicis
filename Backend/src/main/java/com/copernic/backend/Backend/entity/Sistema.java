package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Sistema {

    @Id
    private Long id;

    @Column
    private Double velMax;

    @Column
    private Double tempsMaxAturat;

    @Column
    private int conversioSaldo;

    @Column
    private Duration tempsRecollida;
}
