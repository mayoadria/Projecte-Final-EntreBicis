package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuari")
public class ApiUsuariController {

    @Autowired
    private UsuariLogic logic;
    @PostConstruct
    private void init() {

    }

}
