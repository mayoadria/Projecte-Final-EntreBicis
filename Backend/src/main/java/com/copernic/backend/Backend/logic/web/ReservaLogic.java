package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaLogic {

    @Autowired
    private ReservaRepository reservaRepository;

    public Long saveReserva(Reserva reserva) {
        Reserva ret = reservaRepository.save(reserva);

        return ret.getId();
    }
}
