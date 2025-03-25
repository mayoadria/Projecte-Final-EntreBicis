package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.PuntBescanviLogic;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/recompensas")
public class RecompensaController {

    @Autowired
    private RecompensaLogic logic;
    @Autowired
    private PuntBescanviLogic puntBescanviLogic;

    @GetMapping("/llistar")
    public String LlistarRecompensas(Model model) {
        model.addAttribute("recompensas", logic.llistarRecompensas());
        return "llistarRecompensas";
    }

    @GetMapping("/crear")
    public String Registre(Model model) {
        model.addAttribute("recompensas", new Recompensas());
        model.addAttribute("estat", Estat.values());
        model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
        return "crearRecompensas"; // Vista "Registre"
    }
    @PostMapping("/guardar")
    public String guardarRecompensa(@ModelAttribute("recompensas") Recompensas recompensa, @RequestParam("bescanvi") Long puntBescanviId, Model model) {
        try {
            // Buscar el Punto de Bescanvi en la base de datos
            PuntBescanvi bescanvi = puntBescanviLogic.findByID(puntBescanviId);

            // Asignarlo a la recompensa
            recompensa.setPuntBescanviId(bescanvi);

            // Guardar la recompensa
            logic.guardarRecompensa(recompensa);

            return "redirect:/recompensas/llistar";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    @GetMapping("/delete/{id}")
    public String deleteRecompensa(@PathVariable Long id) {
            logic.eliminarRecompensa(id);

        return "redirect:/recompensas/llistar";

    }

    @GetMapping("/edit/{id}")
    public String editarUsuario(@PathVariable Long id, boolean visualizar, Model model) {
        Recompensas recompensas = logic.findById(id);
        if (recompensas == null) {
            return "redirect:/recompensas/llistar"; // Redirigir si no existe el usuario
        }
        model.addAttribute("recompensas", recompensas);
        model.addAttribute("estat", Estat.values());
        model.addAttribute("bescanvi", puntBescanviLogic.llistarBescanvi());
        return "modificarRecompensa"; // Cargar la vista para editar
    }

    @PostMapping("/editar")
    public String guardarCambios(@ModelAttribute Recompensas recompensa,@RequestParam("bescanvi") Long puntBescanviId,Model model) {
        // Lógica de actualización de recompensa
        Recompensas recompensaExiste = logic.findById(recompensa.getId());

        PuntBescanvi bescanvi = puntBescanviLogic.findByID(puntBescanviId);

        // Asignarlo a la recompensa

        if (recompensaExiste != null) {
            recompensaExiste.setDescripcio(recompensa.getDescripcio());
            recompensaExiste.setCost(recompensa.getCost());
            recompensaExiste.setObservacions(recompensa.getObservacions());
            recompensaExiste.setEstat(recompensa.getEstat());
            recompensaExiste.setPuntBescanviId(bescanvi);

            logic.modificarRecompensa(recompensaExiste);
            return "redirect:/recompensas/llistar";  // Redirigir al listado
        } else {
            model.addAttribute("error", "La recompensa no existe o no es válida.");
            return "modificarRecompensa";  // Mostrar error en la vista
        }
    }



}
