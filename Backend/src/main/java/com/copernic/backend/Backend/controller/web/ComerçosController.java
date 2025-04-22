package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/bescanvi")
public class ComerçosController {
    @Autowired
    private PuntBescanviLogic puntBescanviLogic;


    @GetMapping("/llistar")
    public String llistarComerc(
            Model model,
            @ModelAttribute("error")   String error,
            @ModelAttribute("success") String success
    ) {
        model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
        // Nota: no hace falta hacer nada con `error` y `success`; Thymeleaf
        // ya los podrá resolver (aunque vengan a null).
        return "llistarComerc";
    }


    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("bescanvis", new PuntBescanvi());
        return "crearComerc";
    }

    @PostMapping("/guardar")
    public String guardarComerc(
            @ModelAttribute("bescanvis") PuntBescanvi puntBescanvi,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) {

        try {
            if (fileFoto != null && !fileFoto.isEmpty()) {
                String base64Foto = Base64.getEncoder().encodeToString(fileFoto.getBytes());
                puntBescanvi.setFoto(base64Foto);
            }
            puntBescanviLogic.guardarComerc(puntBescanvi);
            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi creado correctamente.");
            return "redirect:/bescanvi/llistar";

        } catch (Exception e) {
            // Se queda en la misma vista de creación con un error
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "crearComerc";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBescanvi(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            puntBescanviLogic.eliminarBescanvi(id);
            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar, el punt de bescanvi te recompenses assignades");
        }
        return "redirect:/bescanvi/llistar";
    }

    @GetMapping("/edit/{id}")
    public String editarPuntBescanvi(@PathVariable Long id, Model model) {
        PuntBescanvi punt = puntBescanviLogic.findByID(id);
        if (punt == null) {
            model.addAttribute("error", "El punto de bescanvi no existe.");
            return "redirect:/bescanvi/llistar";
        }
        if (punt.getFoto() != null && !punt.getFoto().isEmpty()) {
            model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + punt.getFoto());
        }
        model.addAttribute("bescanvi", punt);
        model.addAttribute("visualizar", false);
        return "modificarComerc";
    }

    @PostMapping("/editar")
    public String guardarCambios(
            @ModelAttribute("bescanvi") PuntBescanvi form,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) {

        PuntBescanvi existente = puntBescanviLogic.findByID(form.getId());
        if (existente == null) {
            model.addAttribute("error", "El punto de bescanvi no existe o no es válido.");
            return "modificarComerc";
        }

        try {
            existente.setNom(form.getNom());
            existente.setAdreca(form.getAdreca());
            existente.setCodiPostal(form.getCodiPostal());
            existente.setTelefon(form.getTelefon());
            existente.setEmail(form.getEmail());
            existente.setPersonaContacte(form.getPersonaContacte());
            existente.setObservacions(form.getObservacions());
            if (fileFoto != null && !fileFoto.isEmpty()) {
                existente.setFoto(Base64.getEncoder().encodeToString(fileFoto.getBytes()));
            }
            puntBescanviLogic.modificarRecompensa(existente);

            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi actualizado correctamente.");
            return "redirect:/bescanvi/llistar";

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar: " + e.getMessage());
            // volver a cargar la foto previa si la hubiera
            if (existente.getFoto() != null) {
                model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + existente.getFoto());
            }
            return "modificarComerc";
        }
    }

    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        PuntBescanvi punt = puntBescanviLogic.findByID(id);
        if (punt == null) {
            model.addAttribute("error", "El punto de bescanvi no existe.");
            return "redirect:/bescanvi/llistar";
        }
        if (punt.getFoto() != null) {
            model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + punt.getFoto());
        }
        model.addAttribute("bescanvi", punt);
        model.addAttribute("visualizar", true);
        return "modificarComerc";
    }
}

