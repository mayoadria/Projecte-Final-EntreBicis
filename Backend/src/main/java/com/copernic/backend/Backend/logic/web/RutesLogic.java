package com.copernic.backend.Backend.logic.web;



import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.RutesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RutesLogic {

    @Autowired
    RutesRepository rutesRepository;

    public void RutesRepository(RutesRepository rutesRepository) {
        this.rutesRepository = rutesRepository;
    }

    public List<Rutes> findAllProducts(){

        List<Rutes> ret;

        ret = rutesRepository.findAll();

        return ret;

    }

    public Long saveRuta(Rutes rutes){

        Rutes ret = rutesRepository.save(rutes);

        return ret.getId();

    }

    public Rutes getRutaById(Long id){

        return rutesRepository.findById(id).orElse(null);

    }

    public void deleteRutaById(Long id){

        rutesRepository.deleteById(id);

    }

    public boolean existsById(Long id)
    {
        Rutes r = rutesRepository.findById(id).orElse(null);

        return (r != null);
    }

    public Rutes findLastByUsuari(Usuari u) {
        return rutesRepository.findTopByUsuariOrderByIdDesc(u)
                .orElse(null);
    }
}