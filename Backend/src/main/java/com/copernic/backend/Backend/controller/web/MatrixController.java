package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sistema")
public class MatrixController {

    private final SistemaLogic sistemaService;

    @GetMapping
    public String mostrarSistema(Model model) {
        // Obtener el sistema desde el servicio
        Sistema sistema = sistemaService.getSistema();
        model.addAttribute("sistema", sistema);
        return "matrix"; // Devolver la vista "matrix" con el objeto Sistema
    }

    @PostMapping("/actualizar")
    public String actualizarSistema(@ModelAttribute Sistema sistema) {
        // Aseguramos que el ID sea 1 al actualizar el sistema
        sistema.setId(1L);

        // Guardamos el sistema usando el servicio
        sistemaService.guardarSistema(sistema);

        // Redirigimos al home
        return "redirect:/home";
    }
}
