package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    @Autowired
    private UsuariLogic usuariLogic;

    @GetMapping
    public ResponseEntity<List<Usuari>> getAllUsuaris() {
        List<Usuari> usuaris = usuariLogic.getAllUsuaris();
        return ResponseEntity.ok(usuaris);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Usuari> getUsuari(@PathVariable String email) {
        Optional<Usuari> usuari = usuariLogic.getUsuariByEmail(email);
        if (usuari.isPresent()) {
            return ResponseEntity.ok(usuari.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuari> createUsuari(@Valid @RequestBody Usuari usuari, BindingResult result) {
        Usuari newUsuari = usuariLogic.createUsuari(usuari);
        return new ResponseEntity<>(newUsuari, HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<Usuari> updateUsuari(@PathVariable String email, @RequestBody Usuari usuari) {
        Optional<Usuari> existingUsuari = usuariLogic.getUsuariByEmail(email);
        if (existingUsuari.isPresent()) {
            Usuari updatedUsuari = usuariLogic.updateUsuari(email, usuari);
            return ResponseEntity.ok(updatedUsuari);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUsuari(@PathVariable String email) {
        Optional<Usuari> existingUsuari = usuariLogic.getUsuariByEmail(email);
        if (existingUsuari.isPresent()) {
            usuariLogic.deleteUsuari(email);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
