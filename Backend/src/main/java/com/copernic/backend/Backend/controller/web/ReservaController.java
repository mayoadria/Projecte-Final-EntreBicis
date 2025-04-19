package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatReserva;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import com.copernic.backend.Backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    @Autowired
    private ReservaLogic reservaLogic;
    @Autowired
    private RecompensaLogic recompensaLogic;

    @Autowired
    private UsuariLogic usuariLogic;

    @Autowired
    private SistemaLogic sistemaLogic;

    @GetMapping("/listar")
    public String showUsuaris(Model model) {
        List<Reserva> reserva = reservaLogic.llistarReserva();
        model.addAttribute("reservas", reserva);
        return "llistarReserva";
    }

    @PostMapping("/canviarEstat")
    public String canviarEstatReserva(@RequestParam Long reservaId, RedirectAttributes redirectAttributes, Model model) {
        Reserva reserva = reservaLogic.findById(reservaId);

        if (reserva == null) {
            redirectAttributes.addFlashAttribute("error", "No s'ha trobat la reserva.");
            return "redirect:/reserva/listar";
        }

        if (reserva.getIdRecompensa() == null) {
            redirectAttributes.addFlashAttribute("error", "La reserva no té recompensa associada.");
            return "redirect:/reserva/listar";
        }

        EstatReserva estatActual = reserva.getEstat();

        switch (estatActual) {
            case RESERVADA:
            case DESASSIGNADA:
                if (!hiHaSaldo(reserva)) {
                    model.addAttribute("errorS", "No hi ha saldo suficient");
                    model.addAttribute("reservas", reservaLogic.llistarReserva());
                    return "llistarReserva";
                }
                assignarReserva(reserva);
                redirectAttributes.addFlashAttribute("missatge", "Estat canviat a ASSIGNADA correctament.");
                break;

            case ASSIGNADA:
                desassignarReserva(reserva);
                redirectAttributes.addFlashAttribute("missatge", "Estat canviat a DESASSIGNADA correctament.");
                break;

            default:
                redirectAttributes.addFlashAttribute("error", "No es pot canviar l'estat d'aquesta reserva.");
        }

        return "redirect:/reserva/listar";
    }


    private boolean hiHaSaldo(Reserva reserva) {
        return reserva.getEmailUsuari().getSaldo() >= reserva.getIdRecompensa().getCost();
    }

    private void assignarReserva(Reserva reserva) {
        reserva.setEstat(EstatReserva.ASSIGNADA);
        reserva.getIdRecompensa().setEstat(Estat.ASSIGNADES);
        reserva.getIdRecompensa().setDataAsignacio(LocalDateTime.now());

        double saldoActual = reserva.getEmailUsuari().getSaldo();
        double cost = reserva.getIdRecompensa().getCost();
        reserva.getEmailUsuari().setSaldo(saldoActual - cost);

        reservaLogic.updateReserva(reserva);
        recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
    }

    private void desassignarReserva(Reserva reserva) {
        reserva.setEstat(EstatReserva.DESASSIGNADA);
        reserva.getIdRecompensa().setEstat(Estat.DISPONIBLES);
        reserva.getIdRecompensa().setDataAsignacio(null);

        double saldoActual = reserva.getEmailUsuari().getSaldo();
        double cost = reserva.getIdRecompensa().getCost();
        reserva.getEmailUsuari().setSaldo(saldoActual + cost);

        reservaLogic.updateReserva(reserva);
        recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
    }

    public boolean haCaducat(Reserva reserva, Duration tempsPermes) {
        LocalDateTime data = reserva.getIdRecompensa().getDataAsignacio();
        if (data == null) return false;
        return data.plus(tempsPermes).isBefore(LocalDateTime.now());
    }

    public void comprovarICaducarReserves() {
        List<Reserva> reserves = reservaLogic.llistarReserva();
        Duration tempsRecollida = sistemaLogic.getSistema().getTempsRecollida();

        for (Reserva reserva : reserves) {
            if (reserva.getEstat() == EstatReserva.ASSIGNADA &&
                    haCaducat(reserva, tempsRecollida)) {

                reserva.setEstat(EstatReserva.DESASSIGNADA);
                reserva.getIdRecompensa().setEstat(Estat.DISPONIBLES);
                reserva.getIdRecompensa().setDataAsignacio(null);
                reserva.setCaducada(true);
                reserva.setEstat(EstatReserva.CADUCADA);
                reserva.getEmailUsuari().setReserva(false);
                reservaLogic.updateReserva(reserva);
                recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
                usuariLogic.savePerfil(reserva.getEmailUsuari());
            }
        }
    }

}
