package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.convert.DurationUnit;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Entitat que representa la configuració general del sistema.
 * Emmagatzema paràmetres globals com la velocitat màxima permesa, límits de temps, conversions de saldo i altres valors configurables.
 * Aquesta configuració afecta el comportament general de la gestió de rutes i recompenses.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Sistema {


    /**
     * Identificador únic del registre de configuració.
     * S'assumeix que el sistema tindrà un únic registre de configuració amb ID predefinit.
     */
    @Id
    private Long id;

    /**
     * Velocitat màxima permesa (en km/h) que es tindrà en compte en el sistema.
     */
    @Column
    private Double velMax;

    /**
     * Temps màxim permès aturat (en minuts) abans de considerar incidències.
     * Annotació {@code @DurationUnit(MINUTES)} per garantir la conversió correcta.
     */
    @Column
    @DurationUnit(MINUTES)
    private Duration tempsMaxAturat;

    /**
     * Factor de conversió per transformar valors en saldo d'usuari.
     */
    @Column
    private int conversioSaldo;

    /**
     * Temps màxim permès per recollir una recompensa (en hores).
     * Annotació {@code @DurationUnit(HOURS)} per garantir la conversió correcta.
     */
    @Column
    @DurationUnit(HOURS)
    private Duration tempsRecollida;

    /**
     * Representació textual de la configuració del sistema.
     *
     * @return String amb les propietats més rellevants de la configuració.
     */
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
