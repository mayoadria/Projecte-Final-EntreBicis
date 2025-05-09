package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ValidadorUsuaris implements UserDetailsService {

    private final UsuariLogic usuariLogic;

    public ValidadorUsuaris(UsuariLogic usuariLogic) {
        this.usuariLogic = usuariLogic;
    }

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
