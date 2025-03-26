package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Base64;

@Controller
@RequestMapping("/usuaris")
public class UsuarisWebController {

    @Autowired
    private UsuariLogic usuariLogic;

    // Listar usuarios, excluyendo administradores
    @GetMapping
    public String showUsuaris(Model model) {
        List<Usuari> usuaris = usuariLogic.getAllUsuaris().stream()
                .filter(u -> u.getRol() != null && !u.getRol().equals(Rol.ADMINISTRADOR))
                .collect(Collectors.toList());
        model.addAttribute("usuaris", usuaris);
        return "usuaris";
    }

    // Mostrar la página para crear un usuario
    @GetMapping("/crear")
    public String showCrearUsuariForm() {
        return "crearUsuari";
    }

    // Crear un usuario asignando el rol CICLISTA y guardando la foto en Base64 (si se selecciona)
    @PostMapping("/crearU")
    public String crearUsuari(Usuari usuari, @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {
        if (fileFoto != null && !fileFoto.isEmpty()) {
            String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
            usuari.setFoto(base64Foto);
        }
        usuari.setRol(Rol.CICLISTA);
        usuariLogic.createUsuari(usuari);
        return "redirect:/usuaris";
    }

    // Mostrar el formulario de edición para un usuario dado su email
    @GetMapping("/editar/{email:.+}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            // Si existe foto, concatenamos en el controlador y la pasamos al modelo
            if (usuari.getFoto() != null && !usuari.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + usuari.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }
            model.addAttribute("usuari", usuari);
            return "editarUsuari";
        } else {
            return "redirect:/usuaris";
        }
    }


    // Procesar la edición y actualizar el usuario en la base de datos.
    // Si se suministra una foto nueva se sobreescribe; si no, se mantiene la anterior.
    @PostMapping("/editar")
    public String editarUsuari(Usuari usuari, @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {
        Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(usuari.getEmail());
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            existing.setNom(usuari.getNom());
            existing.setCognom(usuari.getCognom());
            existing.setContra(usuari.getContra());
            existing.setTelefon(usuari.getTelefon());
            existing.setSaldo(usuari.getSaldo());
            existing.setPoblacio(usuari.getPoblacio());
            if (!existing.getRol().equals(Rol.ADMINISTRADOR)) {
                existing.setRol(Rol.CICLISTA);
            }

            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                existing.setFoto(base64Foto);
            }
            // Si no se envía una nueva foto, se conserva la existente.
            usuariLogic.updateUsuari(existing.getEmail(), existing);
        }
        return "redirect:/usuaris";
    }

    // Eliminar un usuario dado su email
    @GetMapping("/delete/{email:.+}")
    public String deleteUsuari(@PathVariable String email) {
        usuariLogic.deleteUsuari(email);
        return "redirect:/usuaris";
    }
}
