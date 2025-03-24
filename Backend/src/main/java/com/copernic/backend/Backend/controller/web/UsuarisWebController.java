package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuaris")
public class UsuarisWebController {

    @Autowired
    private UsuariLogic usuariLogic;

    // Metodo para listar usuarios, excluyendo administradores y filtrando null
    @GetMapping
    public String showUsuaris(Model model) {
        List<Usuari> usuaris = usuariLogic.getAllUsuaris().stream()
                .filter(u -> u.getRol() != null && !u.getRol().equals(Rol.ADMINISTRADOR))
                .collect(Collectors.toList());
        model.addAttribute("usuaris", usuaris);
        return "usuaris"; // Se busca usuaris.html en src/main/resources/templates/
    }

    // Muestra la página para crear un usuario (crearUsuari.html)
    @GetMapping("/crear")
    public String showCrearUsuariForm() {
        return "crearUsuari";
    }

    // Procesa el formulario de creación y asigna rol CICLISTA
    @PostMapping("/crear")
    public String crearUsuari(Usuari usuari) {
        usuari.setRol(Rol.CICLISTA);
        usuariLogic.createUsuari(usuari);
        return "redirect:/usuaris";
    }

    // Muestra el formulario de edición, capturando el email completo (con .+)
    @GetMapping("/editar/{email:.+}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            model.addAttribute("usuari", usuariOpt.get());
            return "editarUsuari"; // Se buscará editarUsuari.html en templates
        } else {
            return "redirect:/usuaris";
        }
    }


    // Procesa la edición y redirige a la lista de usuarios
    @PostMapping("/editar")
    public String editarUsuari(Usuari usuari) {
        // Recupera el usuario existente usando el email (clave primaria)
        Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(usuari.getEmail());
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            // Sobrescribe los campos con los datos del formulario
            existing.setNom(usuari.getNom());
            existing.setCognom(usuari.getCognom());
            existing.setContra(usuari.getContra());
            existing.setTelefon(usuari.getTelefon());
            existing.setSaldo(usuari.getSaldo());
            existing.setPoblacio(usuari.getPoblacio());
            // Forzamos el rol a CICLISTA (no se permite modificarlo)
            existing.setRol(Rol.CICLISTA);
            // Actualiza el usuario en la base de datos
            usuariLogic.updateUsuari(existing.getEmail(), existing);
        }
        return "redirect:/usuaris";
    }

    @GetMapping("/delete/{email:.+}")
    public String deleteUsuari(@PathVariable String email) {
        usuariLogic.deleteUsuari(email);
        return "redirect:/usuaris";
    }

}
