package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Controlador web per gestionar la configuració del sistema.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/sistema")
public class MatrixController {

    private static final Logger logger = LoggerFactory.getLogger(MatrixController.class);

    private final SistemaLogic sistemaService;

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

    @PostMapping("/actualizar")
    public String actualizarSistema(
            @RequestParam("tempsMaxAturat") String tempsMaxAturatStr,
            @RequestParam("tempsRecollida") String tempsRecollidaStr,
            @RequestParam("velMax") Double velMax,
            @RequestParam("conversioSaldo") Integer conversioSaldo,
            Model model
    ) {
        try {
            Sistema sistema = new Sistema();
            sistema.setId(1L); // Forzar ID = 1

            // Validaciones
            if (!tempsMaxAturatStr.matches("\\d+") || !tempsRecollidaStr.matches("\\d+")) {
                model.addAttribute("errorMessage", "Els valors han de ser números enters positius.");
                return "matrix";
            }

            // Conversión a Duration según contexto
            sistema.setTempsMaxAturat(Duration.of(Long.parseLong(tempsMaxAturatStr), ChronoUnit.MINUTES));
            sistema.setTempsRecollida(Duration.of(Long.parseLong(tempsRecollidaStr), ChronoUnit.HOURS));

            // Otros campos directos
            sistema.setVelMax(velMax);
            sistema.setConversioSaldo(conversioSaldo);

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