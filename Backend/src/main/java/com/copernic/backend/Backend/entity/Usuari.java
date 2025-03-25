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

import javax.validation.constraints.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuari implements UserDetails {

    @NotEmpty(message = "No pot estar buït")
    @Pattern(regexp ="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "El correu electronic no té un format vàlid")
    @Column(unique = true)
    @Id
    private String email;

    @NotEmpty(message = "No pot estar buït")
    @Column
    private String nom;

    @NotEmpty(message = "No pot estar buït")
    @Column
    private String cognom;

    @NotEmpty(message = "No pot estar buïda")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,8}$", message = "El contrasenya no té un format vàlid")
    @Column
    private String contra;

    @NotEmpty(message = "No pot estar buït")
    @Size(min = 9, max = 9, message = "El número de teléfón  té que tenir exactament 9 dígits")
    @Pattern(regexp = "\\d{9}", message = "El número de teléfón només pot contenir dígits")
    @Column
    private String telefon;

    @NotEmpty(message = "No pot estar buïda")
    @Column
    private String poblacio;

    @NotEmpty(message = "No pot estar buït")
    @Pattern(regexp = "\\d{9}", message = "El saldo només pot contenir dígits")
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
