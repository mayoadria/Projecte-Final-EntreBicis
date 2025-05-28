package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servei de validació d'usuaris per a l'autenticació amb Spring Security.
 * Aquesta classe implementa la interfície {@link UserDetailsService} i s'encarrega
 * de carregar les dades de l'usuari des de la base de dades a partir del seu email.
 * També valida que el compte estigui actiu abans de permetre l'accés.
 */
@Service
public class ValidadorUsuaris implements UserDetailsService {

    private final UsuariLogic usuariLogic;

    /**
     * Constructor que injecta la lògica d'usuaris.
     *
     * @param usuariLogic Servei de lògica per gestionar usuaris.
     */
    public ValidadorUsuaris(UsuariLogic usuariLogic) {
        this.usuariLogic = usuariLogic;
    }

    /**
     * Carrega les dades d'un usuari a partir del seu correu electrònic.
     * <p>
     * Si l'usuari no existeix o està inactiu, llença una {@link UsernameNotFoundException}.
     * Si l'usuari és vàlid, retorna un objecte {@link UserDetails} amb les seves credencials i rol.
     * </p>
     *
     * @param email Correu electrònic de l'usuari.
     * @return Detalls de l'usuari per a Spring Security.
     * @throws UsernameNotFoundException Si l'usuari no existeix o està inactiu.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuari usuari = usuariLogic.getUsuariByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb email: " + email));

        if (usuari.getEstat() != EstatUsuari.ACTIU) {
            throw new UsernameNotFoundException("El compte està inactiu.");
        }

        return User.withUsername(usuari.getEmail())
                .password(usuari.getContra())
                .roles(usuari.getRol().name())
                .build();
    }
}
