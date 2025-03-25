package com.copernic.backend;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // Bean para inicializar el usuario admin al arrancar la aplicación
    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@entrebicis.com";
            if (!userRepository.existsById(adminEmail)) {
                Usuari admin = Usuari.builder()
                        .email(adminEmail)
                        .nom("Admin")
                        .cognom("Admin")
                        .contra("admin") // Nota: En producción se debe encriptar
                        .telefon("123456789")
                        .poblacio("Administradorland")
                        .saldo(1000)
                        .rol(Rol.ADMINISTRADOR) // Asegúrate de que tu enum Rol tiene la opción ADMIN
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario admin creado con éxito.");
            } else {
                System.out.println("El usuario admin ya existe.");
            }
        };
    }
}
