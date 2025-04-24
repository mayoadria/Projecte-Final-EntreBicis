package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.controller.web.UsuariController;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariLogic {

    private static final Logger log = LoggerFactory.getLogger(UsuariLogic.class);
    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuari createUsuari(Usuari usuari) {
        if (existeEmail(usuari.getEmail())) {
            throw new ExcepcionEmailDuplicado("El correu electrònic ja està registrat.");
        } else {
            // Encriptar la contraseña antes de guardar el usuario
            usuari.setContra(passwordEncoder.encode(usuari.getContra()));
            return userRepository.save(usuari);
        }
    }

    public List<Usuari> getAllUsuaris() {
        return userRepository.findAll();
    }

    public Optional<Usuari> getUsuariByEmail(String email) {
        return userRepository.findById(email);
    }

    public Usuari getUsuariByEmaiL(String email) {
        return userRepository.findByEmail(email);
    }


    // Actualiza la entidad existente sin modificar el identificador (email)
    public void updateUsuari(Usuari usuariActualitzat) {
        // No se toca el campo "email" ya que es el identificador
        userRepository.save(usuariActualitzat);

    }

    public void deleteUsuari(String email) {
        userRepository.deleteById(email);
    }

    public boolean existeEmail(String email) {
        Usuari usu = userRepository.findById(email).orElse(null);
        return usu != null;
    }

    public Usuari findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    @Transactional
    public String savePerfil(Usuari dto) {

        Usuari db = userRepository.findById(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException());

        db.setNom(dto.getNom());
        db.setCognom(dto.getCognom());
        db.setTelefon(dto.getTelefon());
        db.setPoblacio(dto.getPoblacio());
        db.setReserva(dto.getReserva());
        db.setRuta(dto.getRuta());
        // …cualquier otro campo editable…

        String nuevaPwd = dto.getContra();

        if (nuevaPwd != null && !nuevaPwd.isBlank()) {

            boolean mismaClave = nuevaPwd.equals(db.getContra());

            if (!mismaClave) {
                boolean yaEsHash = nuevaPwd.startsWith("$2");

                if (yaEsHash) {
                    // Llegó un hash distinto → lo ignoramos (podrías lanzar excepción)
                    log.warn("Intento de actualizar la contraseña con un hash ya codificado; se ignora");
                } else {
                    // Llegó texto plano → codificamos
                    db.setContra(passwordEncoder.encode(nuevaPwd));
                }
            }
        }

        userRepository.save(db);
        return db.getEmail();
    }
}
