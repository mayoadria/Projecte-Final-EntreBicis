package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador web per gestionar l'autenticació d'usuaris.
 * <p>
 * Inclou la gestió de login, logout i l'accés a la pàgina d'inici (/home).
 * </p>
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuariLogic usuariLogic;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Redirigeix l'usuari a la pàgina de login.
     *
     * @return Redirecció a la ruta /login.
     */    @GetMapping("/")
    public String redirectToLogin() {
        logger.info("Redirigint a la pàgina de login (/login).");
        return "redirect:/login";
    }
    /**
     * Mostra la pàgina de login.
     * <p>
     * Si s'ha produït un error d'autenticació, es mostra un missatge d'error.
     * </p>
     *
     * @param error Paràmetre opcional per indicar si hi ha hagut un error en l'inici de sessió.
     * @param model Objecte Model per passar dades a la vista.
     * @return Nom de la vista a renderitzar (login o error).
     */
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        try {
            if (error != null) {
                logger.error("Intent fallit d'inici de sessió.");
                model.addAttribute("errorMessage", "Usuari o contrasenya incorrectes.");
            } else {
                logger.error("Accés a la pàgina de login.");
            }
            return "login";
        } catch (Exception e) {
            logger.error("Error carregant el formulari de login.", e);
            model.addAttribute("errorMessage", "Error carregant el formulari de login.");
            return "error";
        }
    }
    /**
     * Realitza el logout de l'usuari autenticat.
     * <p>
     * Si hi ha una sessió activa, es tanca i es redirigeix a la pàgina de login.
     * </p>
     *
     * @param request  Sol·licitud HTTP.
     * @param response Resposta HTTP.
     * @return Redirecció a la ruta /login.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                logger.info("Logout iniciat per l'usuari: {}", auth.getName());
                new SecurityContextLogoutHandler().logout(request, response, auth);
            } else {
                logger.error("S'ha intentat fer logout sense autenticació activa.");
            }
        } catch (Exception e) {
            logger.error("Error durant el logout.", e);
        }
        return "redirect:/login";
    }
    /**
     * Mostra la pàgina d'inici (home) de l'usuari autenticat.
     * <p>
     * Comprova si l'usuari està a la sessió, si no, el busca a la base de dades.
     * Si no es troba, es redirigeix a la pàgina de login.
     * </p>
     *
     * @param model   Objecte Model per passar dades a la vista.
     * @param session Sessió HTTP de l'usuari.
     * @param auth    Objecte d'autenticació actiu.
     * @return Nom de la vista a renderitzar (home, login o error).
     */
    @GetMapping("/home")
    public String showHome(Model model,
                           HttpSession session,
                           Authentication auth) {
        Usuari usuari = null;

        try {
            logger.info("Accés a /home per l'usuari autenticat.");

            // 1) Obtener de la sesión
            Object obj = session.getAttribute("usuari");
            if (obj instanceof Usuari) {
                usuari = (Usuari) obj;
                logger.debug("Usuari recuperat de la sessió: {}", usuari.getEmail());
            }

            // 2) Reponer desde auth si está vacío
            if (usuari == null && auth != null && auth.getName() != null) {
                usuari = usuariLogic.getUsuariByEmail(auth.getName()).orElse(null);
                if (usuari != null) {
                    logger.debug("Usuari recuperat de la base de dades: {}", usuari.getEmail());
                    session.setAttribute("usuari", usuari);
                } else {
                    logger.error("No s'ha pogut trobar l'usuari per email: {}", auth.getName());
                }
            }

            // 3) Si sigue siendo null, redirigir
            if (usuari == null) {
                logger.error("Usuari no disponible en sessió ni base de dades. Redirigint a login.");
                return "redirect:/login";
            }

            model.addAttribute("usuari", usuari);
            return "home";

        } catch (ClassCastException e) {
            logger.error("Error de tipus d'usuari a la sessió.", e);
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error carregant la pàgina d'inici.", e);
            model.addAttribute("errorMessage", "S'ha produït un error inesperat.");
            return "error";
        }
    }

}
