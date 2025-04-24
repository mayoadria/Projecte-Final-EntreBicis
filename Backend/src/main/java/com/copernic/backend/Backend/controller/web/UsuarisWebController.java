package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.Excepciones.ExcepcionEmailDuplicado;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }

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
            usuari.setEstat(EstatUsuari.ACTIU);
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
    @GetMapping("/editar/{email}")
    public String editarUsuariForm(@PathVariable String email, Model model) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            // Si el usuario tiene foto, se crea el data URL
            if (usuari.getFoto() != null && !usuari.getFoto().isEmpty()) {
                String fotoDataUrl = "data:image/jpeg;base64," + usuari.getFoto();
                model.addAttribute("fotoDataUrl", fotoDataUrl);
            }
            model.addAttribute("usuariForm", usuari);
            // En la vista de edición se debe incluir un campo oculto "originalEmail" para evitar modificar el identificador
            return "editarUsuari";
        } else {
            return "redirect:/usuaris";
        }
    }

    // Procesar la edición y actualizar el usuario. Se utiliza el email original para evitar modificar el identificador.
    @PostMapping("/editarUsuari")
    public String editarUsuari( @ModelAttribute("usuariForm") Usuari usuariForm, @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) throws IOException {
        Optional<Usuari> existingOpt = usuariLogic.getUsuariByEmail(usuariForm.getEmail());
        if (existingOpt.isPresent()) {
            Usuari existing = existingOpt.get();
            existing.setNom(usuariForm.getNom());
            existing.setCognom(usuariForm.getCognom());
            // Actualizar la contraseña solo si se proporciona un nuevo valor distinto
            if (usuariForm.getContra() != null && !usuariForm.getContra().isEmpty() &&
                    !passwordEncoder.matches(usuariForm.getContra(), existing.getContra())) {
                existing.setContra(passwordEncoder.encode(usuariForm.getContra()));
            }
            existing.setTelefon(usuariForm.getTelefon());
            existing.setSaldo(usuariForm.getSaldo());
            existing.setPoblacio(usuariForm.getPoblacio());
            if (!existing.getRol().equals(Rol.ADMINISTRADOR)) {
                existing.setRol(Rol.CICLISTA);
            }
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                existing.setFoto(base64Foto);
            }
            usuariLogic.updateUsuari(existing);
        }
        return "redirect:/usuaris";
    }

    // Eliminar un usuario dado su email
    @GetMapping("/delete/{email:.+}")
    public String deleteUsuari(@PathVariable String email,RedirectAttributes redirectAttributes) {
        Usuari usu = usuariLogic.findByEmail(email);
        if(usu.getRuta() || usu.getReserva()){
            redirectAttributes.addFlashAttribute("error", "No es pot eliminar un usuari amb una reserva activa o una ruta activa.");
        }else{
            usuariLogic.deleteUsuari(email);
        }

        return "redirect:/usuaris";
    }

    @GetMapping("/toggleEstat/{email}")
    public String toggleEstatUsuari(@PathVariable String email, RedirectAttributes redirectAttributes) {
        Optional<Usuari> usuariOpt = usuariLogic.getUsuariByEmail(email);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            if (usuari.getEstat() == EstatUsuari.ACTIU) {
                if (usuari.getReserva() == true) {
                    redirectAttributes.addFlashAttribute("error", "No es pot desactivar un usuari amb una reserva activa.");
                    return "redirect:/usuaris";
                }
                usuari.setEstat(EstatUsuari.INACTIU);
            } else {
                usuari.setEstat(EstatUsuari.ACTIU);
            }
            usuariLogic.updateUsuari(usuari);
        }
        return "redirect:/usuaris";
    }


    @InitBinder("usuari")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("email");
    }

}
