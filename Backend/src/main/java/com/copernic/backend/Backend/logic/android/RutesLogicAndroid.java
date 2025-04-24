package com.copernic.backend.Backend.logic.android;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.repository.RutesRepository;

@Service
public class RutesLogicAndroid {

    @Autowired
    private RutesRepository rutesRepository;

    public Rutes save(Rutes ruta) {
        return rutesRepository.save(ruta);
    }
}
