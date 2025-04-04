package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.repository.SistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sistema")
public class MatrixController {

    private final SistemaRepository sistemaRepository;

    @GetMapping
    public String mostrarSistema(Model model) {
        Sistema sistema = sistemaRepository.findById(1L).orElse(new Sistema(1L, 0.0, 0.0, 0, "0"));
        model.addAttribute("sistema", sistema);
        return "matrix";
    }

    @PostMapping("/actualizar")
    public String actualizarSistema(@ModelAttribute Sistema sistema) {
        sistema.setId(1L); // aseguramos que siempre sea ID 1
        sistemaRepository.save(sistema);
        return "redirect:/home";
    }
}
