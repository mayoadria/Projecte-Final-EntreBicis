package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
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

    @GetMapping("/llistar")
    public String LlistarRecompensas(Model model) {
        model.addAttribute("recompensas", logic.llistarRecompensas());
        return "llistarRecompensas";
    }

    @GetMapping("/crear")
    public String Registre(Model model) {
        model.addAttribute("recompensas", new Recompensas());
        model.addAttribute("estat", Estat.values());
        return "crearRecompensas"; // Vista "Registre"
    }
    @PostMapping("/guardar")
    public String guardarRecompensa(@ModelAttribute("recompensas") Recompensas recompensa, Model model) {
        try{

            logic.guardarRecompensa(recompensa);

            return "redirect:/recompensas/llistar";
        }catch (Exception e){
            e.printStackTrace();
            return "error";

        }

    }

    @GetMapping("/delete/{id}")
    public String deleteRecompensa(@PathVariable Long id) {
            logic.eliminarRecompensa(id);

        return "redirect:/recompensas/llistar";

    }


}
