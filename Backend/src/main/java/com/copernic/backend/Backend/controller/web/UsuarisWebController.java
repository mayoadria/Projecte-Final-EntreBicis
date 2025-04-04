package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Controller
@RequestMapping("/usuaris")
public class UsuarisWebController {

    @Autowired
    private UsuariLogic usuariLogic;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public String showCrearUsuariForm(Model model) {
        // Se añade una nueva instancia para asegurar que el formulario se vincule a un objeto nuevo
        model.addAttribute("newUsuari", new Usuari());
        return "crearUsuari";
    }

    // Crear un usuario asignando el rol CICLISTA y guardando la foto en Base64 (si se selecciona)
    @PostMapping("/crearUsuari")
    public String crearUsuari(@ModelAttribute("newUsuari") Usuari usuari,
                              @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                              Model model) {
        try {
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                usuari.setFoto(base64Foto);
            }
            usuari.setRol(Rol.CICLISTA);
            usuari.setEstat(Estat.ACTIU);
            usuariLogic.createUsuari(usuari);
            return "redirect:/usuaris";
        } catch (ExcepcionEmailDuplicado e) {
            model.addAttribute("error", e.getMessage());
            // Devolver el objeto con los datos ingresados para que se repopule el formulario
            model.addAttribute("newUsuari", usuari);
            return "crearUsuari";
        } catch (IOException ex) {
            model.addAttribute("error", "Error procesando la imagen.");
            model.addAttribute("newUsuari", usuari);
            return "crearUsuari";
        }
    }


    // Mostrar el formulario de edición para un usuario dado su email
    @GetMapping("/editar/{email:.+}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            // Si el usuario tiene foto, se crea el data URL
            if (usuari.getFoto() != null && !usuari.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + usuari.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }
            model.addAttribute("usuari", usuari);
            // En la vista de edición se debe incluir un campo oculto "originalEmail" para evitar modificar el identificador
            return "editarUsuari";
        } else {
            return "redirect:/usuaris";
        }
    }

    // Procesar la edición y actualizar el usuario. Se utiliza el email original para evitar modificar el identificador.
    @PostMapping("/editarUsuari")
    public String editarUsuari(@RequestParam("originalEmail") String originalEmail, Usuari usuari, @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {
        Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(originalEmail);
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            existing.setNom(usuari.getNom());
            existing.setCognom(usuari.getCognom());
            // Actualizar la contraseña solo si se proporciona un nuevo valor distinto
            if (usuari.getContra() != null && !usuari.getContra().isEmpty() &&
                    !passwordEncoder.matches(usuari.getContra(), existing.getContra())) {
                existing.setContra(passwordEncoder.encode(usuari.getContra()));
            }
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
            usuariLogic.updateUsuari(originalEmail, existing);
        }
        return "redirect:/usuaris";
    }

    // Eliminar un usuario dado su email
    @GetMapping("/delete/{email:.+}")
    public String deleteUsuari(@PathVariable String email) {
        usuariLogic.deleteUsuari(email);
        return "redirect:/usuaris";
    }

    @PostMapping("/toggleActivation/{email:.+}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> toggleActivation(
            @PathVariable String email,
            @RequestBody Map<String, String> payload) {

        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            try {
                Estat nouEstat = Estat.valueOf(payload.get("estat"));
                usuari.setEstat(nouEstat);
                usuariLogic.updateUsuari(email, usuari);
                return ResponseEntity.ok(Collections.singletonMap("estat", nouEstat.name()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Estat no vàlid"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Usuari no trobat"));
        }
    }

    @InitBinder("usuari")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("email");
    }

}
