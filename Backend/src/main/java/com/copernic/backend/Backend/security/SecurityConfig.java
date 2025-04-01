package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import com.copernic.backend.Backend.repository.SistemaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ValidadorUsuaris validadorUsuaris;
    private final UsuariLogic usuariLogic;
    private final SistemaRepository sistemaRepository;

    public SecurityConfig(ValidadorUsuaris validadorUsuaris,
                          UsuariLogic usuariServiceSQL,
                          UserDetailsService userDetailsService,
                          SistemaRepository sistemaRepository) {
        this.validadorUsuaris = validadorUsuaris;
        this.usuariLogic = usuariServiceSQL;
        this.sistemaRepository = sistemaRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/styles/**", "/images/**", "/api/**").permitAll()
                        .requestMatchers("/home/**").hasRole("ADMINISTRADOR") // Solo admins
                        .requestMatchers("/recompensas/**").hasRole("ADMINISTRADOR") // Solo admins
                        .requestMatchers("/bescanvi/**").hasRole("ADMINISTRADOR") // Solo admins
                        .requestMatchers("/usuaris/**").hasRole("ADMINISTRADOR") // Solo admins
                        // Usuarios y admins
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("contra")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .userDetailsService(validadorUsuaris);

        crearAdminSiNoExiste();
        crearSistemaSiNoExiste();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void crearAdminSiNoExiste() {
        Optional<Usuari> adminExistente = usuariLogic.getUsuariByEmail("admin@entrebicis.com");
        if (adminExistente.isPresent()) {
            System.out.println("El administrador ya existe.");
            return;
        }

        Usuari admin = new Usuari();
        admin.setContra("admin");
        admin.setCognom("admin");
        admin.setNom("admin");
        admin.setPoblacio("admin");
        admin.setSaldo(20);
        admin.setEmail("admin@entrebicis.com");
        admin.setTelefon("000000000");
        admin.setRol(Rol.ADMINISTRADOR);
        admin.setEstat(Estat.ACTIU);

        usuariLogic.createUsuari(admin);
    }

    private void crearSistemaSiNoExiste() {
        Optional<Sistema> sistemaExistente = sistemaRepository.findById(1L);
        if (sistemaExistente.isPresent()) {
            System.out.println("El sistema ya existe.");
            return;
        }

        Sistema sistema = new Sistema();
        sistema.setId(1L); // Forzamos el ID=1
        sistema.setVelMax(20.0);          // Valor por defecto (ajusta según tus necesidades)
        sistema.setTempsMaxAturat(10.0);    // Valor por defecto
        sistema.setConversioSaldo(2);       // Valor por defecto
        sistema.setTempsRecollida("60");    // Valor por defecto

        sistemaRepository.save(sistema);
        System.out.println("Sistema por defecto creado con ID=1.");
    }
}
