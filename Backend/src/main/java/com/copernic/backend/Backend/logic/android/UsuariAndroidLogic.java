package com.copernic.backend.Backend.logic.android;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuariAndroidLogic {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuari updateUsuari(String email, Usuari usuari) throws Exception {
        Optional<Usuari> optionalUser = userRepository.findById(email);
        if(optionalUser.isPresent()){
            Usuari existing = optionalUser.get();
            // Actualiza únicamente los campos modificables desde Android
            existing.setNom(usuari.getNom());
            existing.setCognom(usuari.getCognom());
            // Actualizamos la contraseña si se proporciona un nuevo valor no vacío
            if (usuari.getContra() != null && !usuari.getContra().isEmpty()) {
                existing.setContra(passwordEncoder.encode(usuari.getContra()));
            }
            existing.setTelefon(usuari.getTelefon());
            existing.setPoblacio(usuari.getPoblacio());
            existing.setSaldo(usuari.getSaldo());
            existing.setFoto(usuari.getFoto());
            return userRepository.save(existing);
        } else {
            throw new Exception("Usuari no trobat");
        }
    }
}
