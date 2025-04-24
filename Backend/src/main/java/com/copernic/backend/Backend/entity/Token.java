package com.copernic.backend.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_code", nullable = false)
    private String tokenCode;

    @Column(name = "exp_date", nullable = false)
    private LocalDateTime expireDate;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Usuari usuari;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireDate);
    }

    public Token(String tokenCode, Usuari usuari) {
        this.tokenCode = tokenCode;
        this.usuari = usuari;
        this.expireDate = LocalDateTime.now().plusMinutes(10);
    }
}