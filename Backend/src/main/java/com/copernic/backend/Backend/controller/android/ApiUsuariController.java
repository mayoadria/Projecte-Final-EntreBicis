package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.android.UsuariAndroidLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuari")
public class ApiUsuariController {

    @Autowired
    private UsuariAndroidLogic logic;

    @PostConstruct
    private void init() {
        // Inicialización si es necesaria
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
