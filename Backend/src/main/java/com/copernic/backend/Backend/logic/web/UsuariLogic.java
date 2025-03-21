package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuariLogic {

    @Autowired
    private UserRepository userRepository;

    public Usuari createUsuari(Usuari usuari) {
        return userRepository.save(usuari);
    }

    public List<Usuari> getAllUsuaris() {
        return userRepository.findAll();
    }

    public Optional<Usuari> getUsuariByEmail(String email) {
        return userRepository.findById(email);
    }

    public Usuari updateUsuari(String email, Usuari usuari) {
        // Asumimos que el usuari existe; en el controlador se puede validar la existencia.
        usuari.setEmail(email); // Aseguramos que la clave primaria se mantiene
        return userRepository.save(usuari);
    }

    public void deleteUsuari(String email) {
        userRepository.deleteById(email);
    }
}
