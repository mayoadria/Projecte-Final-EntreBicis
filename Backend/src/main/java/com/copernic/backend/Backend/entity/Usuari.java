    package com.copernic.backend.Backend.entity;

    import com.copernic.backend.Backend.entity.enums.Estat;
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
    import java.util.stream.Collectors;

    import javax.validation.constraints.*;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public class Usuari implements UserDetails {


        @Column(unique = true)
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
        private Double saldo;

        @Builder.Default
        @Column(nullable = false)
        private Boolean reserva = false;

//        @Column(nullable = true)
//        private Boolean ruta;
        @Lob
        private String foto; // Se guardará la cadena Base64
        @Enumerated(EnumType.STRING)
        private Rol rol;

        @Enumerated(EnumType.STRING)
        private EstatUsuari estat;

        @OneToMany(mappedBy = "usuari", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JsonManagedReference
        @ToString.Exclude
        private List<Rutes> rutes;

        @OneToMany(mappedBy = "usuariRecompensa", cascade = CascadeType.ALL)
        @JsonManagedReference
        @ToString.Exclude
        private List<Recompensas> recompensas;

        @OneToMany(mappedBy = "emailUsuari", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @ToString.Exclude
        @JsonIgnore
        private List<Reserva> reservas;


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

        @Override
        public int hashCode() {
            return Objects.hash(email, nom, cognom, telefon, poblacio, rol, estat); // Ajusta según los atributos relevantes
        }
    }
