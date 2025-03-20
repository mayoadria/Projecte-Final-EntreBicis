package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuari {
    @Id
    private String email;
    @Column
    private String nom;
    @Column
    private String cognom;
    @Column
    private String contra;
    @Column
    private String telefon;
    @Column
    private String poblacio;
    @Column
    private int saldo;
    @Lob
    private byte[] foto;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "usuari", cascade = CascadeType.ALL)
    private List<Rutes> rutes;

    @OneToMany(mappedBy = "usuariRecompensa", cascade = CascadeType.ALL)
    private List<Recompensas> recompensas;



}
