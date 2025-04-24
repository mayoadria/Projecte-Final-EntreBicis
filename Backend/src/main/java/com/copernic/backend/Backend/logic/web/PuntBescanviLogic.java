package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.logic.android.PuntBescanviLogicAndroid;
import com.copernic.backend.Backend.repository.BescanviRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuntBescanviLogic {

    @Autowired
    BescanviRepository bescanviRepository;
    @Autowired
    private PuntBescanviLogicAndroid puntBescanviLogicAndroid;

    public List<PuntBescanvi> llistarBescanvi() {
        return bescanviRepository.findAll();
    }

    public PuntBescanvi findByID(Long id) {
        return bescanviRepository.findById(id).get();
    }

    public void guardarComerc(PuntBescanvi bescanvi) {
        bescanviRepository.save(bescanvi);
    }

    // Servicio
    public void eliminarBescanvi(Long id) {
        PuntBescanvi puntBescanvi = findByID(id);
        if (puntBescanvi == null) {
            throw new EntityNotFoundException("Punto de bescanvi no existe: " + id);
        }
        // Si hay integridad referencial (FK) u otro fallo, propaga la excepción
        bescanviRepository.delete(puntBescanvi);
    }

    public void modificarRecompensa(PuntBescanvi bescanvi) {
        bescanviRepository.save(bescanvi);
    }
}
