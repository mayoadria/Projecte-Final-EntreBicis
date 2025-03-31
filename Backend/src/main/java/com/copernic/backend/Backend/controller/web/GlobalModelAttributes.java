package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UsuariLogic usuariLogic;

    @ModelAttribute
    public void addAdminToModel(Model model) {
        Usuari admin = usuariLogic.getUsuariByEmail("admin@entrebicis.com").orElse(null);
        model.addAttribute("usuari", admin);
    }
}
