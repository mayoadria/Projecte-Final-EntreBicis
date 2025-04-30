package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import com.copernic.backend.Backend.repository.SistemaRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.time.Duration;
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

    /* 1.  Cadena exclusiva API – stateless, sin redirecciones */
    @Bean @Order(1)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll())      // o authenticated()
                /* Evita redirigir a /login cuando falte auth:
                   responde 401 / 403 en crudo  */
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req,res,e) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req,res,e) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /* 2.  Cadena web (formularios) */
    @Bean @Order(2)
    public SecurityFilterChain webChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout",
                                "/styles/**", "/images/**",
                                "/error"              //  ←  nuevo
                        ).permitAll()
                        .anyRequest().hasRole("ADMINISTRADOR"))
                        .formLogin(f -> f.loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("contra")
                        .defaultSuccessUrl("/home", true))
                .logout(l -> l.logoutUrl("/logout")
                        .logoutSuccessUrl("/login"));
        crearAdminSiNoExiste();
        crearSistemaSiNoExiste();
        return http.build();
    }

    /* 3.  Permitir ‘;’ en la URL  */
    @Bean
    public HttpFirewall allowSemicolonFirewall() {
        StrictHttpFirewall fw = new StrictHttpFirewall();
        fw.setAllowSemicolon(true);
        return fw;
    }


//    @Bean
//    public HttpFirewall allowSemicolonHttpFirewall() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setAllowSemicolon(true); // Permite ';' en las URLs
//        return firewall;
//    }
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
        admin.setSaldo(20.0);
        admin.setEmail("admin@entrebicis.com");
        admin.setTelefon("000000000");
        admin.setRol(Rol.ADMINISTRADOR);
        admin.setEstat(EstatUsuari.ACTIU);
        admin.setReserva(false);
        //admin.setRuta(true);

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
        sistema.setTempsMaxAturat(Duration.ofMinutes(5));    // Valor por defecto
        sistema.setConversioSaldo(2);       // Valor por defecto
        sistema.setTempsRecollida(Duration.ofHours(72));    // Valor por defecto

        sistemaRepository.save(sistema);
        System.out.println("Sistema por defecto creado con ID=1.");
    }
}
