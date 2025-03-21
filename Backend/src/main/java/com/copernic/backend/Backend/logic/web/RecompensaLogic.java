package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecompensaLogic {

    @Autowired
    private RecompensasRepository recompensasRepository;


    public void guardarRecompensa(Recompensas recompensa) {
        recompensasRepository.save(recompensa);
    }

    public List<Recompensas> llistarRecompensas() {
        return recompensasRepository.findAll();
    }

    public void eliminarRecompensa(Recompensas recompensa) {
        recompensasRepository.delete(recompensa);
    }

    public void modificarRecompensa(Recompensas recompensa) {
        recompensasRepository.save(recompensa);
    }

}
