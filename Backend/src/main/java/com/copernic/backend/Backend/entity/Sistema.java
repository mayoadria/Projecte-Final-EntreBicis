package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

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
    @DurationUnit(MINUTES)   // <- aquí dices “trátalo como minutos”
    private Duration tempsMaxAturat;

    @Column
    private int conversioSaldo;

    @Column
    @DurationUnit(HOURS)     // <- y aquí “como horas”
    private Duration tempsRecollida;

    @Override
    public String toString() {
        return "Sistema{" +
                "id=" + id +
                ", velMax=" + velMax +
                ", tempsMaxAturat=" + tempsMaxAturat +
                ", conversioSaldo=" + conversioSaldo +
                ", tempsRecollida=" + (tempsRecollida != null ? tempsRecollida.toHours() + " hores" : "null") +
                '}';
    }
}
