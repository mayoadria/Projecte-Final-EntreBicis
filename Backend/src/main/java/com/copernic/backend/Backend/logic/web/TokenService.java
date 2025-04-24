package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Token;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    private TokenRepo tokenRepo;

    public void saveToken(Token token) {
        tokenRepo.save(token);
    }

    public void deleteToken(Token token) {
        tokenRepo.delete(token);
    }

    public Optional<Token> getByClient(Usuari usuari) {
        return tokenRepo.findByUsuari(usuari);
    }

    public Optional<Token> getByToken(String token) {
        return tokenRepo.findByTokenCode(token);
    }
}
