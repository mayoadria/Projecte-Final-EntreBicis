package com.copernic.backend.Backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class DurationConfig implements WebMvcConfigurer {

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
