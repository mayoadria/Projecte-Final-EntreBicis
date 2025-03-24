package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuaris")
public class UsuarisWebController {

    @Autowired
    private UsuariLogic usuariLogic;

    // Muestra la página para crear un usuario (crearUsuari.html)
    @GetMapping("/crear")
    public String showCrearUsuariForm() {
        return "crearUsuari"; // Busca el archivo crearUsuari.html en templates
    }

    // Recibe el envío del formulario y crea el usuario en la base de datos
    @PostMapping("/crear")
    public String crearUsuari(Usuari usuari) {
        // Aquí se realiza la conversión de los datos enviados y se crea el usuario
        usuari.setRol(Rol.CICLISTA);
        usuariLogic.createUsuari(usuari);
        // Después de crear, redirige a la lista de usuarios
        return "redirect:/usuaris";
    }
}
