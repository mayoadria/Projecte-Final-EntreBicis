package com.copernic.backend.Backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuració personalitzada per permetre la conversió automàtica de cadenes de text a objectes {@link Duration} en Spring MVC.
 * Aquesta classe registra un conversor que interpreta diferents formats de durada:
 *     Números simples (es consideren minuts per defecte).
 *     Formats amb sufixos (ex: 5m per minuts, 2h per hores).
 *     Formats ISO-8601 (ex: PT10M, PT2H).
 *
 *
 */
@Configuration
public class DurationConfig implements WebMvcConfigurer {

    /**
     * Registra un conversor de {@link String} a {@link Duration} al FormatterRegistry de Spring.
     * El conversor suporta els següents formats d'entrada:
     *     Només números → es converteixen a minuts per defecte (ex: "15" = 15 minuts).
     *     Cadenes acabades en 'm' o 'M' → minuts explícits (ex: "5m" o "10M").
     *     Cadenes acabades en 'h' o 'H' → hores explícites (ex: "6h" o "24H").
     *     Formats ISO-8601 estàndard (ex: "PT30M", "PT3H").
     *
     * @param registry Registre de formatadors i converters de Spring.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, Duration.class, source -> {
            if (source == null || source.isBlank()) return null;

            // Sólo número → minutos por defecto
            if (source.matches("\\d+")) {
                return Duration.of(Long.parseLong(source), ChronoUnit.MINUTES);
            }
            // 5m ó 10M
            if (source.matches("\\d+[mM]")) {
                return Duration.of(Long.parseLong(source.replaceAll("[mM]", "")), ChronoUnit.MINUTES);
            }
            // 6h ó 24H
            if (source.matches("\\d+[hH]")) {
                return Duration.of(Long.parseLong(source.replaceAll("[hH]", "")), ChronoUnit.HOURS);
            }
            // Formato ISO-8601 (PT5M, PT72H, etc.)
            return Duration.parse(source);
        });
    }
}
