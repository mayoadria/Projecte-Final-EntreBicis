package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String showHome(Model model,
                           HttpSession session,          // ⬅ traemos la sesión
                           Authentication auth) {        // ⬅ por si necesitas repoblar

        // 1) Intenta sacar el objeto desde la sesión (lo metiste en /user)
        Usuari usuari = (Usuari) session.getAttribute("usuari");

        // 2) Si la sesión caducó pero el SecurityContext sigue vivo, repón el atributo
        if (usuari == null && auth != null) {
            usuari = usuariLogic.getUsuariByEmail(auth.getName()).orElse(null);
            session.setAttribute("usuari", usuari);
        }

        // 3) Sin usuario ⇒ vuelve a /login
        if (usuari == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuari", usuari);   // por si tu plantilla principal lo usa
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
