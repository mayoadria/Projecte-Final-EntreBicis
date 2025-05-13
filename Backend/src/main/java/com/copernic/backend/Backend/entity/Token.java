package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entitat que representa un token d'autenticació temporal associat a un usuari.
 * Utilitzat per processos com la verificació d'email, restabliment de contrasenya o accions de seguretat que requereixen validesa limitada.
 */
@Entity
@Data
@NoArgsConstructor
public class Token {

    /**
     * Identificador únic del token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Codi alfanumèric que representa el token.
     */
    @Column(name = "token_code", nullable = false)
    private String tokenCode;

    /**
     * Data i hora de caducitat del token.
     */
    @Column(name = "exp_date", nullable = false)
    private LocalDateTime expireDate;

    /**
     * Usuari associat a aquest token.
     * Relació OneToOne amb {@link Usuari}.
     */
    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Usuari usuari;

    /**
     * Comprova si el token ha caducat comparant la data d'expiració amb el moment actual.
     *
     * @return {@code true} si el token ha caducat, {@code false} si encara és vàlid.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireDate);
    }

    /**
     * Constructor per crear un nou token amb validesa de 10 minuts a partir del moment de creació.
     *
     * @param tokenCode Codi identificador del token.
     * @param usuari Usuari associat al token.
     */
    public Token(String tokenCode, Usuari usuari) {
        this.tokenCode = tokenCode;
        this.usuari = usuari;
        this.expireDate = LocalDateTime.now().plusMinutes(10);
    }
}
