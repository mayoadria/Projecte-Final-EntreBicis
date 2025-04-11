package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.logic.web.RutesLogic;
import com.copernic.backend.Backend.repository.RutesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ruta")
public class ApiRutaController {

    @PostConstruct
    private void init()
    {

    }

    @Autowired
    private RutesLogic rutesLogic;

    @GetMapping("/read/all")
    public ResponseEntity<List<Rutes>> findAll(){

        List<Rutes> llista;

        //el transporte HTTP
        ResponseEntity<List<Rutes>> response;

        //la cabecera del transporte
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {


            llista = productLogic.findAllProducts();

            response = new ResponseEntity<>(llista, headers, HttpStatus.OK);

        } catch (Exception e) {

            logger.error("Error rading all products: " + e);

            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

}
