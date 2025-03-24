package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public String showUsuaris(Model model) {
        // Obtiene todos los usuarios y filtra los que no son administradores
        List<Usuari> usuaris = usuariLogic.getAllUsuaris().stream()
                .filter(u -> !u.getRol().equals(Rol.ADMINISTRADOR)) // Ajusta el valor según tu enum (por ejemplo, ADMIN o ADMINISTRADOR)
                .collect(Collectors.toList());
        model.addAttribute("usuaris", usuaris);
        return "usuaris"; // Se buscará usuaris.html en src/main/resources/templates/
    }


    @GetMapping("/crearUsuaris")
    public String crearUsuariForm() {
        return "crearUsuari"; // crear-usuari.html
    }


}
