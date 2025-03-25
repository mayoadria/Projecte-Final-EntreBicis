package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuari implements UserDetails {
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



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return contra; // Devuelve la contraseña real
    }

    @Override
    public String getUsername() {
        return email; // Devuelve el email como username
    }

}
