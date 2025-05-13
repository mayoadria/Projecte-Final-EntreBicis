package com.copernic.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal de l'aplicació backend.
 * Aquesta classe inicialitza l'aplicació Spring Boot i habilita la planificació de tasques periòdiques mitjançant {@link EnableScheduling}.
 * Carrega la configuració i els components de l'aplicació.
 * Executa el context d'execució de Spring.
 * Habilita l'execució de mètodes anotats amb {@code @Scheduled}.
 */
@EnableScheduling
@SpringBootApplication
public class BackendApplication {
    /**
     * Punt d'entrada principal de l'aplicació.
     * Executa el context de Spring Boot amb la configuració definida.
     *
     * @param args Arguments de línia de comandes.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


}
