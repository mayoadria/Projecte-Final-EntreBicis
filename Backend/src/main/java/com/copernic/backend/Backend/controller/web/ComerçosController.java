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

/**
 * Controlador web per gestionar els punts de bescanvi (comerços).
 * <p>
 * Permet llistar, crear, editar, eliminar i visualitzar punts de bescanvi.
 * </p>
 */
@Controller
@RequestMapping("/bescanvi")
public class ComerçosController {

    private static final Logger logger = LoggerFactory.getLogger(ComerçosController.class);

    @Autowired
    private PuntBescanviLogic puntBescanviLogic;
    /**
     * Mostra la llista de punts de bescanvi.
     * <p>
     * Permet filtrar per nom si s'indica el paràmetre corresponent.
     * </p>
     *
     * @param model   Model per passar dades a la vista.
     * @param error   Missatge d'error (opcional).
     * @param success Missatge d'èxit (opcional).
     * @param nomPunt Nom del punt per filtrar la llista (opcional).
     * @return Vista amb la llista de punts de bescanvi.
     */
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
    /**
     * Mostra el formulari per crear un nou punt de bescanvi.
     *
     * @param model Model per passar dades a la vista.
     * @return Vista del formulari de creació.
     */
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("bescanvis", new PuntBescanvi());
        return "crearComerc";
    }
    /**
     * Guarda un nou punt de bescanvi.
     * <p>
     * També gestiona la pujada d'una imatge associada en format Base64.
     * </p>
     *
     * @param puntBescanvi        Dades del punt de bescanvi a guardar.
     * @param model               Model per passar dades a la vista.
     * @param redirectAttributes  Per mostrar missatges després de redireccions.
     * @param fileFoto            Imatge adjunta (opcional).
     * @return Redirecció a la llista o vista d'error.
     */
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
            redirectAttributes.addFlashAttribute("success", "Punt de bescanvi creat correctament.");
            return "redirect:/bescanvi/llistar";

        } catch (Exception e) {
            logger.error("Error al guardar el punto de bescanvi", e);
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "crearComerc";
        }
    }
    /**
     * Elimina un punt de bescanvi pel seu ID.
     *
     * @param id                  Identificador del punt a eliminar.
     * @param redirectAttributes  Per mostrar missatges després de redireccions.
     * @return Redirecció a la llista de punts de bescanvi.
     */
    @GetMapping("/delete/{id}")
    public String deleteBescanvi(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            puntBescanviLogic.eliminarBescanvi(id);
            logger.info("PuntBescanvi eliminado con ID: {}", id);
            redirectAttributes.addFlashAttribute("success", "Punt de bescanvi eliminat correctament.");
        } catch (Exception e) {
            logger.error("Error al eliminar punto de bescanvi con ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar, el punt de bescanvi té recompenses assignades.");
        }
        return "redirect:/bescanvi/llistar";
    }
    /**
     * Mostra el formulari per editar un punt de bescanvi existent.
     *
     * @param id    Identificador del punt a editar.
     * @param model Model per passar dades a la vista.
     * @return Vista del formulari d'edició o redirecció si hi ha error.
     */
    @GetMapping("/edit/{id}")
    public String editarPuntBescanvi(@PathVariable Long id, Model model) {
        try {
            PuntBescanvi punt = puntBescanviLogic.findByID(id);
            if (punt == null) {
                logger.error("Intento de edición de punto no existente, ID: {}", id);
                model.addAttribute("error", "El punt de bescanvi no existeix.");
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
    /**
     * Guarda els canvis realitzats en un punt de bescanvi existent.
     * <p>
     * També permet actualitzar la imatge associada.
     * </p>
     *
     * @param form                Dades del punt de bescanvi actualitzades.
     * @param model               Model per passar dades a la vista.
     * @param redirectAttributes  Per mostrar missatges després de redireccions.
     * @param fileFoto            Nova imatge (opcional).
     * @return Redirecció a la llista o vista d'error.
     */
    @PostMapping("/editar")
    public String guardarCambios(
            @ModelAttribute("bescanvi") PuntBescanvi form,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto) {

        PuntBescanvi existente = puntBescanviLogic.findByID(form.getId());
        if (existente == null) {
            logger.error("Intento de actualizar un punto inexistente: ID {}", form.getId());
            model.addAttribute("error", "El punt de bescanvi no existeix o no es válid.");
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

            redirectAttributes.addFlashAttribute("success", "Punt de bescanvi actualizat correctament.");
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
    /**
     * Mostra la vista de detall d'un punt de bescanvi en mode només lectura.
     *
     * @param id    Identificador del punt a visualitzar.
     * @param model Model per passar dades a la vista.
     * @return Vista amb els detalls del punt de bescanvi o redirecció si hi ha error.
     */
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
