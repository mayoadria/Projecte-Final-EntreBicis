package com.copernic.backend.Backend.repository;

import com.copernic.backend.Backend.entity.Token;
import com.copernic.backend.Backend.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Optional<Token> findByUsuari(Usuari client);

    Optional<Token> findByTokenCode(String tokenCode);
}
