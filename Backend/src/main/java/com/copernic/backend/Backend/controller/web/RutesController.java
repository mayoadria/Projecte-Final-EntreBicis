package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.dto.RouteCardDTO;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.RutesLogic;
import com.copernic.backend.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de administración que muestra, como máximo, un “card” por usuario
 * con su última ruta (o vacío si aún no ha salido en bici).  Límite 50 usuarios.
 */
@Controller
@RequiredArgsConstructor
public class RutesController {

    private final UserRepository usuariRepository;
    private final RutesLogic       rutesLogic;

    @GetMapping("/admin/rutes")
    public String rutes(Model model) {

        // 1. Traemos la lista (máx. 50 usuarios)
        List<Usuari> usuaris = usuariRepository.findAll();
        if (usuaris.size() > 50) {
            usuaris = usuaris.subList(0, 50);
        }

        // 2. Para cada usuario buscamos su última ruta (puede devolver null)
        List<RouteCardDTO> cards = usuaris.stream()
                .map(u -> {
                    Rutes last = rutesLogic.findLastByUsuari(u);   // nuevo método en la lógica
                    return new RouteCardDTO(u, last);              // DTO con toda la info que tu plantilla necesita
                })
                .collect(Collectors.toList());

        // 3. Enviamos a la vista Thymeleaf
        model.addAttribute("routeCards", cards);
        return "rutes";   // templates/rutes.html
    }
}
