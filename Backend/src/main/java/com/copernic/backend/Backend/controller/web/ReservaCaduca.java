package com.copernic.backend.Backend.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component que gestiona la caducitat de les reserves.
 * <p>
 * Aquesta classe executa periòdicament una revisió de les reserves per comprovar i actualitzar el seu estat si han caducat.
 * </p>
 */
@Component
public class ReservaCaduca {

    /**
     * Controlador de reserves que proporciona la lògica per verificar i caducar reserves.
     */
    @Autowired
    private ReservaController reservaService;

    /**
     * Mètode programat que s'executa automàticament cada minut.
     * <p>
     * S'encarrega de revisar totes les reserves i caducar aquelles que han superat el temps límit.
     * </p>
     */
    @Scheduled(fixedRate = 60000) // Cada minuto
    public void revisarReservesCaducades() {
        reservaService.comprovarICaducarReserves();
    }
}
