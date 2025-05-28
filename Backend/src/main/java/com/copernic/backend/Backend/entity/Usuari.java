package com.copernic.backend.Backend.entity;

import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuari implements UserDetails {


    /**
     * Correu electrònic de l'usuari, que actua com a identificador únic.
     */
    @Column(unique = true)
    @Id
    private String email;

    /**
     * Nom de l'usuari.
     */
    @Column
    private String nom;

    /**
     * Cognom de l'usuari.
     */
    @Column
    private String cognom;

    /**
     * Contrasenya encriptada de l'usuari.
     */
    @Column
    private String contra;

    /**
     * Telèfon de contacte de l'usuari.
     */
    @Column
    private String telefon;

    /**
     * Població de residència de l'usuari.
     */
    @Column
    private String poblacio;

    /**
     * Saldo actual de l'usuari (en punts).
     */
    @Column
    private Double saldo;

    /**
     * Indica si l'usuari té una reserva activa.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean reserva = false;

    /**
     * Fotografia de l'usuari emmagatzemada com a cadena Base64.
     */
    @Lob
    private String foto;

    @Column
    private String observaciones;

    /**
     * Rol de l'usuari dins del sistema (CICLISTA, ADMINISTRADOR).
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;

    /**
     * Estat d'activació de l'usuari (ACTIU, INACTIU).
     */
    @Enumerated(EnumType.STRING)
    private EstatUsuari estat;

    /**
     * Llista de rutes associades a l'usuari.
     * Relació OneToMany amb {@link Rutes}.
     */
    @OneToMany(mappedBy = "usuari", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Rutes> rutes;

    /**
     * Llista de recompenses associades a l'usuari.
     * Relació OneToMany amb {@link Recompensas}.
     */
    @OneToMany(mappedBy = "usuariRecompensa", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private List<Recompensas> recompensas;

    /**
     * Llista de reserves realitzades per l'usuari.
     * Relació OneToMany amb {@link Reserva}.
     */
    @OneToMany(mappedBy = "emailUsuari", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<Reserva> reservas;

    /**
     * Retorna les autoritats (roles) de l'usuari per a Spring Security.
     *
     * @return Col·lecció d'autoritats de l'usuari.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    /**
     * Obté la contrasenya encriptada de l'usuari.
     *
     * @return Contrasenya encriptada.
     */
    @Override
    public String getPassword() {
        return contra; // Devuelve la contraseña real
    }

    /**
     * Obté el nom d'usuari per a l'autenticació (corresponent a l'email).
     *
     * @return Correu electrònic de l'usuari.
     */
    @Override
    public String getUsername() {
        return email; // Devuelve el email como username
    }

    /**
     * Compara si dos usuaris són iguals segons les seves dades principals.
     *
     * @param o Objecte a comparar.
     * @return {@code true} si són iguals, {@code false} si no.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuari usuari = (Usuari) o;
        return Objects.equals(email, usuari.email) &&
                Objects.equals(nom, usuari.nom) &&
                Objects.equals(cognom, usuari.cognom) &&
                Objects.equals(telefon, usuari.telefon) &&
                Objects.equals(poblacio, usuari.poblacio) &&
                Objects.equals(rol, usuari.rol) &&
                Objects.equals(estat, usuari.estat); // Excluye 'saldo' y 'foto' si no son relevantes
    }

    /**
     * Calcula el hashCode de l'usuari a partir dels seus camps rellevants.
     *
     * @return Valor hashCode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, nom, cognom, telefon, poblacio, rol, estat); // Ajusta según los atributos relevantes
    }
}
