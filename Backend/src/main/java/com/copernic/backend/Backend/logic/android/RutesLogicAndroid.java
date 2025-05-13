package com.copernic.backend.Backend.logic.android;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.repository.RutesRepository;

/**
 * Classe de lògica de negoci per gestionar les rutes des de l'app Android.
 * Aquesta classe encapsula la interacció amb el repositori {@link RutesRepository}
 * i proporciona operacions bàsiques per persistir rutes al sistema.
 */
@Service
public class RutesLogicAndroid {

    /**
     * Repositori per a l'accés a dades de l'entitat {@link Rutes}.
     */
    @Autowired
    private RutesRepository rutesRepository;

    /**
     * Desa una ruta a la base de dades.
     *
     * @param ruta Objecte {@link Rutes} a desar.
     * @return La ruta desada, incloent l'identificador generat.
     */
    public Rutes save(Rutes ruta) {
        return rutesRepository.save(ruta);
    }
}
