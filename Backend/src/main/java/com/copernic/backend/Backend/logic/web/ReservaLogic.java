package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaLogic {

    @Autowired
    private ReservaRepository reservaRepository;

    public Long saveReserva(Reserva reserva) {
        Reserva ret = reservaRepository.save(reserva);

        return ret.getId();
    }

    public List<Reserva> llistarReserva() {
        return reservaRepository.findAll();
    }

    public Reserva findById(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public void updateReserva(Reserva usuariActualitzat) {
        // No se toca el campo "email" ya que es el identificador
        reservaRepository.save(usuariActualitzat);

    }
    public boolean existsById(Long id)
    {
        Reserva p = reservaRepository.findById(id).orElse(null);

        return (p != null);
    }

    public void deleteReservaById(Long id){

        reservaRepository.deleteById(id);

    }
}
