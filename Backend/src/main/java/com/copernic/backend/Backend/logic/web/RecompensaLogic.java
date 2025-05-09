package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Recompensas> llistarRecompensasAndroid(){

        List<Recompensas> ret= new ArrayList<>();

        ret = recompensasRepository.findAll();

        return ret;

    }

    public String eliminarRecompensa(Long id) {
        Recompensas recompensas = findById(id);
        try {
            if (recompensas != null && recompensas.getEstat() == Estat.DISPONIBLES) {
                recompensasRepository.delete(recompensas);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Eliminado";
    }

    public void modificarRecompensa(Recompensas recompensa) {
        recompensasRepository.save(recompensa);
    }

    public Recompensas findById(Long id) {
        return recompensasRepository.findById(id).get();
    }

    public boolean existeRecompensa(Long email) {
        Recompensas usu = recompensasRepository.findById(email).orElse(null);
        return usu != null;
    }

    public List<Recompensas> llistarPerPunt(Long puntId) {
        return recompensasRepository.findByPuntBescanviId_Id(puntId);
    }

    public List<Recompensas> llistarPerEmailUsuari(String email) {
        return recompensasRepository.findByUsuariRecompensa_Email(email);
    }



}
