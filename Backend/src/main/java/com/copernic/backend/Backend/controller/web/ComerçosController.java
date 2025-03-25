package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bescanvi")
public class ComerçosController {
    @Autowired
    private PuntBescanviLogic puntBescanviLogic;

    @GetMapping("/llistar")
    public String LlistarComerc(Model model) {
        model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
        return "llistarComerc";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("bescanvis", new PuntBescanvi());
        return "crearComerc"; // Vista "Registre"
    }
    @PostMapping("/guardar")
    public String guardarComerc(@ModelAttribute("bescanvis") PuntBescanvi puntBescanvi ,Model model) {
        try {

            // Guardar la recompensa
            puntBescanviLogic.guardarComerc(puntBescanvi);

            return "redirect:/bescanvi/llistar";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    @GetMapping("/delete/{id}")
    public String deleteBescanvi(@PathVariable Long id) {
        puntBescanviLogic.eliminarBescanvi(id);

        return "redirect:/bescanvi/llistar";

    }

    @GetMapping("/edit/{id}")
    public String editarPuntBescanvi(@PathVariable Long id, Model model) {
        PuntBescanvi puntBescanvi = puntBescanviLogic.findByID(id);
        if (puntBescanvi == null) {
            return "redirect:/bescanvi/llistar"; // Redirigir si no existe el usuario
        }
        model.addAttribute("bescanvi", puntBescanvi);
        return "modificarComerc"; // Cargar la vista para editar
    }

    @PostMapping("/editar")
    public String guardarCambios(@ModelAttribute PuntBescanvi bescanvi,Model model) {
        // Lógica de actualización de recompensa
        PuntBescanvi puntBescanviExistent = puntBescanviLogic.findByID(bescanvi.getId());


        if (puntBescanviExistent != null) {
            puntBescanviExistent.setAdreca(bescanvi.getAdreca());
            puntBescanviExistent.setNom(bescanvi.getNom());
            puntBescanviExistent.setObservacions(bescanvi.getObservacions());
            puntBescanviExistent.setEmail(bescanvi.getEmail());
            puntBescanviExistent.setTelefon(bescanvi.getTelefon());
            puntBescanviExistent.setCodiPostal(bescanvi.getCodiPostal());
            puntBescanviExistent.setPersonaContacte(bescanvi.getPersonaContacte());

            puntBescanviLogic.modificarRecompensa(puntBescanviExistent);
            return "redirect:/bescanvi/llistar";  // Redirigir al listado
        } else {
            model.addAttribute("error", "La recompensa no existe o no es válida.");
            return "modificarRecompensa";  // Mostrar error en la vista
        }
    }
}
