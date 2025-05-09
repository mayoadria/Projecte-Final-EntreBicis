package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bescanvi")
public class ComerçosController {

    private static final Logger logger = LoggerFactory.getLogger(ComerçosController.class);

    @Autowired
    private PuntBescanviLogic puntBescanviLogic;

    @GetMapping("/llistar")
    public String llistarComerc(
            Model model,
            @ModelAttribute("error") String error,
            @ModelAttribute("success") String success,
            @RequestParam(name = "nomPunt", required = false) String nomPunt
    ) {
        try {
            List<PuntBescanvi> bescanvis = puntBescanviLogic.llistarBescanvi();
            if (nomPunt != null && !nomPunt.isEmpty()) {
                bescanvis = bescanvis.stream()
                        .filter(r -> r.getNom().toLowerCase().contains(nomPunt.toLowerCase()))
                        .collect(Collectors.toList());
            }
            model.addAttribute("bescanvi", bescanvis);
            return "llistarComerc";
        } catch (Exception e) {
            logger.error("Error al listar puntos de bescanvi", e);
            model.addAttribute("error", "No se pudo cargar la lista de comerços.");
            return "llistarComerc";
        }
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
            logger.info("PuntBescanvi creado: {}", puntBescanvi.getNom());
            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi creado correctamente.");
            return "redirect:/bescanvi/llistar";

        } catch (Exception e) {
            logger.error("Error al guardar el punto de bescanvi", e);
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "crearComerc";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBescanvi(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            puntBescanviLogic.eliminarBescanvi(id);
            logger.info("PuntBescanvi eliminado con ID: {}", id);
            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi eliminado correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar punto de bescanvi con ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar, el punt de bescanvi té recompenses assignades.");
        }
        return "redirect:/bescanvi/llistar";
    }

    @GetMapping("/edit/{id}")
    public String editarPuntBescanvi(@PathVariable Long id, Model model) {
        try {
            PuntBescanvi punt = puntBescanviLogic.findByID(id);
            if (punt == null) {
                logger.error("Intento de edición de punto no existente, ID: {}", id);
                model.addAttribute("error", "El punto de bescanvi no existe.");
                return "redirect:/bescanvi/llistar";
            }

            if (punt.getFoto() != null && !punt.getFoto().isEmpty()) {
                model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + punt.getFoto());
            }

            model.addAttribute("bescanvi", punt);
            model.addAttribute("visualizar", false);
            return "modificarComerc";

        } catch (Exception e) {
            logger.error("Error al cargar la edición del punto de bescanvi con ID: {}", id, e);
            model.addAttribute("error", "No se pudo cargar el formulario de edición.");
            return "redirect:/bescanvi/llistar";
        }
    }

    @PostMapping("/editar")
    public String guardarCambios(
            @ModelAttribute("bescanvi") PuntBescanvi form,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) {

        PuntBescanvi existente = puntBescanviLogic.findByID(form.getId());
        if (existente == null) {
            logger.error("Intento de actualizar un punto inexistente: ID {}", form.getId());
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
            logger.info("PuntBescanvi actualizado: ID {}", existente.getId());

            redirectAttributes.addFlashAttribute("success", "Punto de bescanvi actualizado correctamente.");
            return "redirect:/bescanvi/llistar";

        } catch (Exception e) {
            logger.error("Error al actualizar punto de bescanvi con ID: {}", existente.getId(), e);
            model.addAttribute("error", "Error al actualizar: " + e.getMessage());
            if (existente.getFoto() != null) {
                model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + existente.getFoto());
            }
            return "modificarComerc";
        }
    }

    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        try {
            PuntBescanvi punt = puntBescanviLogic.findByID(id);
            if (punt == null) {
                logger.error("Intento de visualizar un punto inexistente: ID {}", id);
                model.addAttribute("error", "El punto de bescanvi no existe.");
                return "redirect:/bescanvi/llistar";
            }

            if (punt.getFoto() != null) {
                model.addAttribute("fotoDataUrl", "data:image/jpeg;base64," + punt.getFoto());
            }

            model.addAttribute("bescanvi", punt);
            model.addAttribute("visualizar", true);
            return "modificarComerc";

        } catch (Exception e) {
            logger.error("Error al visualizar punto de bescanvi con ID: {}", id, e);
            model.addAttribute("error", "No se pudo cargar la vista de detalles.");
            return "redirect:/bescanvi/llistar";
        }
    }
}
