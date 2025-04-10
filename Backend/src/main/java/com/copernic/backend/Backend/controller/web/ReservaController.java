package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatReserva;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
import com.copernic.backend.Backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    @Autowired
    private ReservaLogic reservaLogic;
    @Autowired
    private RecompensaLogic recompensaLogic;

    @GetMapping("/listar")
    public String showUsuaris(Model model) {
        List<Reserva> reserva = reservaLogic.llistarReserva();
        model.addAttribute("reservas", reserva);
        return "llistarReserva";
    }

    @PostMapping("/canviarEstat")
    public String canviarEstatReserva(@RequestParam Long reservaId, RedirectAttributes redirectAttributes) {
        // Buscar la reserva por ID
        Reserva reserva = reservaLogic.findById(reservaId);

        // Comprobar si la reserva existe
        if (reserva != null) {
            // Obtener el estado actual de la reserva
            EstatReserva estatActual = reserva.getEstat();

            // Comprobar si la reserva tiene recompensa asociada
            if (reserva.getIdRecompensa() == null) {
                redirectAttributes.addFlashAttribute("error", "La reserva no tiene recompensa asociada.");
                return "redirect:/reserva/listar";
            }

            // Cambiar el estado dependiendo del estado actual
            if (estatActual == EstatReserva.RESERVADA) {
                // Si el estado es "RESERVADA", lo cambiamos a "ASSIGNADA"
                reserva.setEstat(EstatReserva.ASSIGNADA);
                reserva.getIdRecompensa().setEstat(Estat.ASSIGNADES);
                reserva.getIdRecompensa().setDataAsignacio(LocalDate.now().toString());
                reservaLogic.updateReserva(reserva);
                recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
                redirectAttributes.addFlashAttribute("missatge", "Estat canviat a ASSIGNADA correctament.");

            } else if (estatActual == EstatReserva.ASSIGNADA) {
                // Si el estado es "ASSIGNADA", lo cambiamos a "DESASSIGNADA"
                reserva.setEstat(EstatReserva.DESASSIGNADA);
                reserva.getIdRecompensa().setEstat(Estat.DISPONIBLES);
                reserva.getIdRecompensa().setDataAsignacio("");  // Limpiar la fecha de asignación
                reservaLogic.updateReserva(reserva);
                recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
                redirectAttributes.addFlashAttribute("missatge", "Estat canviat a DESASSIGNADA correctament.");

            } else if (estatActual == EstatReserva.DESASSIGNADA) {
                // Si el estado es "DESASSIGNADA", lo cambiamos a "ASSIGNADA"
                reserva.setEstat(EstatReserva.ASSIGNADA);
                reserva.getIdRecompensa().setEstat(Estat.ASSIGNADES);
                reserva.getIdRecompensa().setDataAsignacio(LocalDate.now().toString());  // Limpiar la fecha de asignación
                reservaLogic.updateReserva(reserva);
                recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());  // Solo actualizamos la reserva, sin tocar la recompensa
                redirectAttributes.addFlashAttribute("missatge", "Estat canviat a ASSIGNADA correctament.");

            } else {
                // Si el estado no es ninguno de los tres anteriores, mostramos un mensaje de error
                redirectAttributes.addFlashAttribute("error", "No es pot canviar l'estat d'aquesta reserva.");
            }

        } else {
            // Si la reserva no se encuentra
            redirectAttributes.addFlashAttribute("error", "No s'ha trobat la reserva.");
        }

        // Redirigir a la lista de reservas
        return "redirect:/reserva/listar";
    }
}
