package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Usuari o contrasenya incorrectes.");
        }
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

    @GetMapping("/crearUsuaris")
    public String crearUsuariForm() {
        return "crearUsuari"; // crear-usuari.html
    }


}
