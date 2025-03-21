package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuariLogic usuariLogic;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // Ahora se utiliza "email" para autenticar
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        Optional<Usuari> admin = usuariLogic.getUsuariByEmail(email);
        if (admin.isPresent()
                && admin.get().getContra().equals(password)
                && admin.get().getRol().name().equals("ADMINISTRADOR")) {
            return "redirect:/home";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }


    @GetMapping("/usuaris")
    public String showUsuaris() {
        return "usuaris";
    }

}
