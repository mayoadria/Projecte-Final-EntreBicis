package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // Ahora se utiliza "email" para autenticar
    @PostMapping("/user")
    public String login(@RequestParam("email") String email,
                        @RequestParam("contra") String contra) {
        Optional<Usuari> admin = usuariLogic.getUsuariByEmail(email);
        if (admin.isPresent() &&
                passwordEncoder.matches(contra, admin.get().getContra()) &&
                admin.get().getRol().name().equals("ADMINISTRADOR")) {
            return "redirect:/home";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la autenticación actual del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si el usuario está autenticado, cierra la sesión
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/login"; // Redirige a la página principal
    }

    @GetMapping("/home")
    public String showHome(Model model) {
        // Recupera el usuario administrador, por ejemplo:
        Usuari admin = usuariLogic.getUsuariByEmail("admin@entrebicis.com").orElse(null);
        model.addAttribute("admin", admin);
        return "home";
    }



//    @GetMapping("/usuaris")
//    public String showUsuaris(Model model) {
//        // Obtiene todos los usuarios y filtra los que no son administradores
//        List<Usuari> usuaris = usuariLogic.getAllUsuaris().stream()
//                .filter(u -> !u.getRol().equals(Rol.ADMINISTRADOR)) // Ajusta el valor según tu enum (por ejemplo, ADMIN o ADMINISTRADOR)
//                .collect(Collectors.toList());
//        model.addAttribute("usuaris", usuaris);
//        return "usuaris"; // Se buscará usuaris.html en src/main/resources/templates/
//    }


    @GetMapping("/crearUsuaris")
    public String crearUsuariForm() {
        return "crearUsuari"; // crear-usuari.html
    }


}
