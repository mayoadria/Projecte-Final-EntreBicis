package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Crear un usuario asignando el rol CICLISTA y guardando la foto (si se selecciona)
    @PostMapping("/crear")
    public String crearUsuari(Usuari usuari, @RequestParam(value = "foto", required = false) MultipartFile foto) {
        if (foto != null && !foto.isEmpty()) {
            try {
                usuari.setFoto(foto.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        usuari.setRol(Rol.CICLISTA);
        usuariLogic.createUsuari(usuari);
        return "redirect:/usuaris";
    }

    // Endpoint para servir la imagen de perfil almacenada en la BD
    @GetMapping("/photo/{email:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getFoto(@PathVariable String email) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent() && usuariOpt.get().getFoto() != null) {
            byte[] foto = usuariOpt.get().getFoto();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Se asume JPEG; si pueden ser de otro tipo, deberás almacenarlo y ajustar este valor
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Mostrar el formulario de edición para un usuario dado su email
    @GetMapping("/editar/{email:.+}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            model.addAttribute("usuari", usuariOpt.get());
            return "editarUsuari";
        } else {
            return "redirect:/usuaris";
        }
    }

    // Procesar la edición y actualizar el usuario en la base de datos
    @PostMapping("/editar")
    public String editarUsuari(Usuari usuari, @RequestParam(value = "foto", required = false) MultipartFile foto) {
        Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(usuari.getEmail());
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            existing.setNom(usuari.getNom());
            existing.setCognom(usuari.getCognom());
            existing.setContra(usuari.getContra());
            existing.setTelefon(usuari.getTelefon());
            existing.setSaldo(usuari.getSaldo());
            existing.setPoblacio(usuari.getPoblacio());
            // Se fuerza el rol a CICLISTA, ya que no debe modificarse
            existing.setRol(Rol.CICLISTA);
            if (foto != null && !foto.isEmpty()) {
                try {
                    existing.setFoto(foto.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
