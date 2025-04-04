package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuariLogic {

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

    // Actualiza la entidad existente sin modificar el identificador (email)
    public Usuari updateUsuari(String email, Usuari usuariActualitzat) {
        Optional<Usuari> existingOpt = userRepository.findById(email);
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            // Actualizar solo los campos modificables
            existing.setNom(usuariActualitzat.getNom());
            existing.setCognom(usuariActualitzat.getCognom());
            existing.setContra(usuariActualitzat.getContra());
            existing.setTelefon(usuariActualitzat.getTelefon());
            existing.setSaldo(usuariActualitzat.getSaldo());
            existing.setPoblacio(usuariActualitzat.getPoblacio());
            existing.setFoto(usuariActualitzat.getFoto());
            // No se toca el campo "email" ya que es el identificador
            return userRepository.save(existing);
        } else {
            throw new RuntimeException("Usuari no trobat");
        }
    }

    public void deleteUsuari(String email) {
        userRepository.deleteById(email);
    }

    public boolean existeEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Usuari findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }
}
