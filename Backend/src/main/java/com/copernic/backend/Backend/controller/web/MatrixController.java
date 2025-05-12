package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador web per gestionar la configuració del sistema.
 * <p>
 * Permet visualitzar i actualitzar els paràmetres del sistema des de la vista web.
 * </p>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/sistema")
public class MatrixController {

    private static final Logger logger = LoggerFactory.getLogger(MatrixController.class);

    private final SistemaLogic sistemaService;
    /**
     * Mostra la configuració actual del sistema.
     *
     * @param model Model per passar les dades a la vista.
     * @return Vista amb la configuració del sistema o una vista d'error si falla.
     */
    @GetMapping
    public String mostrarSistema(Model model) {
        try {
            Sistema sistema = sistemaService.getSistema();
            model.addAttribute("sistema", sistema);
            return "matrix";
        } catch (Exception e) {
            logger.error("Error al mostrar el sistema", e);
            model.addAttribute("errorMessage", "No es pot carregar la configuració del sistema.");
            return "error";
        }
    }
    /**
     * Actualitza la configuració del sistema amb les dades proporcionades.
     * <p>
     * Sempre actualitza el sistema amb ID = 1.
     * </p>
     *
     * @param sistema Objecte Sistema amb les dades actualitzades.
     * @param model   Model per passar missatges a la vista.
     * @return Redirecció a la pàgina d'inici o una vista d'error si falla.
     */
    @PostMapping("/actualizar")
    public String actualizarSistema(@ModelAttribute Sistema sistema, Model model) {
        try {
            sistema.setId(1L); // forzar ID = 1
            sistemaService.guardarSistema(sistema);
            logger.info("Sistema actualizado correctamente.");
            return "redirect:/home";
        } catch (Exception e) {
            logger.error("Error al actualizar el sistema", e);
            model.addAttribute("errorMessage", "No s'ha pogut actualitzar la configuració.");
            return "error";
        }
    }
}