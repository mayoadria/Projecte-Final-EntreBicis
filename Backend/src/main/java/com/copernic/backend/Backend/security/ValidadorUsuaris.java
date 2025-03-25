package com.copernic.backend.Backend.security;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidadorUsuaris implements UserDetailsService {

    private final UsuariLogic usuariLogic;

    public ValidadorUsuaris(UsuariLogic usuariLogic) {
        this.usuariLogic = usuariLogic;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuariLogic.getUsuariByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat o no activat"));
    }
}
