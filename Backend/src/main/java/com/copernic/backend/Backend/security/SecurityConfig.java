package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UsuariLogic usuariLogic;

    public SecurityConfig(ValidadorUsuaris validadorUsuaris, UsuariLogic usuariServiceSQL,UserDetailsService userDetailsService) {
        this.validadorUsuaris = validadorUsuaris;
        this.usuariLogic = usuariServiceSQL;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/styles/**", "/images/**").permitAll()
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
                ).userDetailsService(validadorUsuaris);

        crearAdminSiNoExiste();

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
            System.out.println("El administrador ja existeix.");
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
}

