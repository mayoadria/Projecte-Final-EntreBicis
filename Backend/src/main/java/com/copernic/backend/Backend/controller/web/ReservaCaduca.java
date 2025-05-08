package com.copernic.backend.Backend.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaCaduca {

    @Autowired
    private ReservaController reservaService;

    @Scheduled(fixedRate = 60000) // Cada minuto
    public void revisarReservesCaducades() {
        reservaService.comprovarICaducarReserves();
    }
}
