package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Token;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Servei de lògica de negoci per gestionar tokens d'autenticació.
 * Proporciona operacions per desar, eliminar i consultar tokens associats a usuaris.
 * Utilitzat per processos de verificació, autenticació o accions de seguretat temporal.
 */
@Service
public class TokenService {
    /**
     * Repositori per a l'accés a dades de {@link Token}.
     */
    @Autowired
    private TokenRepo tokenRepo;

    /**
     * Desa un token a la base de dades.
     *
     * @param token Objecte {@link Token} a desar.
     */
    public void saveToken(Token token) {
        tokenRepo.save(token);
    }

    /**
     * Elimina un token de la base de dades.
     *
     * @param token Objecte {@link Token} a eliminar.
     */
    public void deleteToken(Token token) {
        tokenRepo.delete(token);
    }

    /**
     * Busca un token associat a un usuari concret.
     *
     * @param usuari Objecte {@link Usuari} pel qual es busca el token.
     * @return {@link Optional} amb el token trobat, o buit si no existeix.
     */
    public Optional<Token> getByClient(Usuari usuari) {
        return tokenRepo.findByUsuari(usuari);
    }

    /**
     * Busca un token a partir del seu codi.
     *
     * @param token Codi identificador del token.
     * @return {@link Optional} amb el token trobat, o buit si no existeix.
     */
    public Optional<Token> getByToken(String token) {
        return tokenRepo.findByTokenCode(token);
    }
}
