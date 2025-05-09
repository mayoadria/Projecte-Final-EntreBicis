package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatReserva;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

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
        try {
            List<Reserva> reserva = reservaLogic.llistarReserva();
            model.addAttribute("reservas", reserva);
            return "llistarReserva";
        } catch (Exception e) {
            logger.error("Error al listar reservas", e);
            model.addAttribute("errorMessage", "No s'han pogut carregar les reserves.");
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaLogic.deleteReservaById(id);
            logger.info("Reserva eliminada amb ID: {}", id);
            redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctament.");
        } catch (Exception e) {
            logger.error("Error al eliminar reserva amb ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar la reserva.");
        }
        return "redirect:/reserva/listar";
    }

    @PostMapping("/canviarEstat")
    public String canviarEstatReserva(@RequestParam Long reservaId,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        try {
            Reserva reserva = reservaLogic.findById(reservaId);
            if (reserva == null) {
                logger.error("Reserva no trobada amb ID: {}", reservaId);
                redirectAttributes.addFlashAttribute("error", "No s'ha trobat la reserva.");
                return "redirect:/reserva/listar";
            }

            if (reserva.getIdRecompensa() == null) {
                logger.error("Reserva sense recompensa associada. ID: {}", reservaId);
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
                    logger.error("Intent de canvi d'estat no vàlid. Estat actual: {}", estatActual);
                    redirectAttributes.addFlashAttribute("error", "No es pot canviar l'estat d'aquesta reserva.");
            }

        } catch (Exception e) {
            logger.error("Error al canviar l'estat de la reserva", e);
            redirectAttributes.addFlashAttribute("error", "Error intern en canviar l'estat de la reserva.");
        }

        return "redirect:/reserva/listar";
    }

    private boolean hiHaSaldo(Reserva reserva) {
        return reserva.getEmailUsuari().getSaldo() >= reserva.getIdRecompensa().getCost();
    }

    private void assignarReserva(Reserva reserva) {
        try {
            reserva.setEstat(EstatReserva.ASSIGNADA);
            reserva.getIdRecompensa().setEstat(Estat.ASSIGNADES);
            reserva.getIdRecompensa().setDataAsignacio(LocalDateTime.now());

            double saldoActual = reserva.getEmailUsuari().getSaldo();
            double cost = reserva.getIdRecompensa().getCost();
            reserva.getEmailUsuari().setSaldo(saldoActual - cost);

            reservaLogic.updateReserva(reserva);
            recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
            usuariLogic.savePerfil(reserva.getEmailUsuari());

            logger.info("Reserva assignada amb ID: {}", reserva.getId());
        } catch (Exception e) {
            logger.error("Error assignant reserva amb ID: {}", reserva.getId(), e);
        }
    }

    private void desassignarReserva(Reserva reserva) {
        try {
            reserva.setEstat(EstatReserva.DESASSIGNADA);
            reserva.getIdRecompensa().setEstat(Estat.DISPONIBLES);
            reserva.getIdRecompensa().setDataAsignacio(null);
            reserva.getEmailUsuari().setReserva(false);

            double saldoActual = reserva.getEmailUsuari().getSaldo();
            double cost = reserva.getIdRecompensa().getCost();
            reserva.getEmailUsuari().setSaldo(saldoActual + cost);

            reservaLogic.updateReserva(reserva);
            recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
            usuariLogic.savePerfil(reserva.getEmailUsuari());

            logger.info("Reserva desassignada amb ID: {}", reserva.getId());
        } catch (Exception e) {
            logger.error("Error desassignant reserva amb ID: {}", reserva.getId(), e);
        }
    }

    public boolean haCaducat(Reserva reserva, Duration tempsPermes) {
        LocalDateTime data = reserva.getIdRecompensa().getDataAsignacio();
        return data != null && data.plus(tempsPermes).isBefore(LocalDateTime.now());
    }

    public void comprovarICaducarReserves() {
        try {
            List<Reserva> reserves = reservaLogic.llistarReserva();
            Duration tempsRecollida = sistemaLogic.getSistema().getTempsRecollida();

            for (Reserva reserva : reserves) {
                if (reserva.getEstat() == EstatReserva.ASSIGNADA &&
                        haCaducat(reserva, tempsRecollida)) {

                    reserva.setEstat(EstatReserva.CADUCADA);
                    reserva.getIdRecompensa().setEstat(Estat.DISPONIBLES);
                    reserva.getIdRecompensa().setDataAsignacio(null);
                    reserva.setCaducada(true);
                    reserva.getEmailUsuari().setReserva(false);

                    reservaLogic.updateReserva(reserva);
                    recompensaLogic.modificarRecompensa(reserva.getIdRecompensa());
                    usuariLogic.savePerfil(reserva.getEmailUsuari());

                    logger.info("Reserva caducada amb ID: {}", reserva.getId());
                }
            }

        } catch (Exception e) {
            logger.error("Error al comprovar i caducar reserves", e);
        }
    }
}
