package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/recompensa")
public class ApiRecompensesController {

    @Autowired
    private RecompensaLogic logic;

    @PostConstruct
    private void init() {
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recompensas>> findAll() {

        List<Recompensas> recompensas;

        ResponseEntity<List<Recompensas>> response;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", " no-store");

        try {
            recompensas = logic.llistarRecompensasAndroid();

            response = new ResponseEntity<>(recompensas, headers, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<Recompensas> findByID(@PathVariable Long id) {
        Recompensas recompensas;
        ResponseEntity<Recompensas> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", " no-store");

        try {
            recompensas = logic.findById(id);
            if (recompensas == null) {
                response = new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            } else {
                response = new ResponseEntity<>(recompensas, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateRecompensa(@RequestBody Recompensas recompensas) {

        ResponseEntity<Void> resposta;

        try {
            if (recompensas != null) {

                if (logic.existeRecompensa(recompensas.getId())) {

                    logic.guardarRecompensa(recompensas);
                    resposta = ResponseEntity.ok().build();
                } else {
                    resposta = ResponseEntity.notFound().build();
                }
            } else {
                resposta = ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            resposta = ResponseEntity.internalServerError().build();
        }
        return resposta;
    }
}
