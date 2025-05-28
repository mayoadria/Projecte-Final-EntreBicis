package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Classe que defineix atributs globals per a totes les vistes del projecte.
 * <p>
 * S'encarrega d'afegir automàticament l'usuari administrador al model de totes les vistes.
 * </p>
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UsuariLogic usuariLogic;
    /**
     * Afegeix l'usuari administrador al model de totes les vistes.
     * <p>
     * Busca l'usuari amb email "admin@entrebicis.com" i el posa com a atribut "usuari".
     * </p>
     *
     * @param model Model per passar dades a les vistes.
     */
    @ModelAttribute
    public void addAdminToModel(Model model) {
        Usuari admin = usuariLogic.getUsuariByEmail("admin@entrebicis.com").orElse(null);
        model.addAttribute("usuari", admin);
    }
}
