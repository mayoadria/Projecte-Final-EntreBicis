package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.android.UsuariAndroidLogic;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuari")
public class ApiUsuariController {

    @Autowired
    private UsuariLogic logic;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        // Inicialización si es necesaria
    }


    @GetMapping("/validar/{email}/{contra}")
    public ResponseEntity<Usuari> validateUser(@PathVariable String email, @PathVariable String contra) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {
            Optional<Usuari> optionalUsuari = logic.getUsuariByEmail(email);

            if (optionalUsuari.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Usuari usuari = optionalUsuari.get();

            // Verifica la contraseña usando el PasswordEncoder
            if (passwordEncoder.matches(contra, usuari.getContra())) {
                return new ResponseEntity<>(usuari, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/editar/{email}")
    public ResponseEntity<?> updateUsuari(@PathVariable String email, @RequestBody Usuari usuari) {
        try {
            Usuari usuariActualitzat = logic.updateUsuari(email, usuari);
            return new ResponseEntity<>(usuariActualitzat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error actualitzant usuari: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
