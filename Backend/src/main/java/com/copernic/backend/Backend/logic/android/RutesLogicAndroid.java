package com.copernic.backend.Backend.logic.android;

import java.util.List;
import java.util.Optional;

import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
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

    @Autowired
    private UsuariLogic usuariLogic;

    /** Guarda una ruta (ya existente) */
    /**
     * Desa una ruta a la base de dades.
     *
     * @param ruta Objecte {@link Rutes} a desar.
     * @return La ruta desada, incloent l'identificador generat.
     */
    public Rutes save(Rutes ruta) {
        return rutesRepository.save(ruta);
    }

    /** Nuevo: devuelve todas las rutas de un usuario, ordenadas desc. */
    public List<Rutes> findByUsuariEmail(String email) {
        Optional<Usuari> opt = usuariLogic.getUsuariByEmail(email);
        Usuari u = opt.orElseThrow(() ->
                new RuntimeException("Usuario no encontrado: " + email)
        );
        // El repositorio expone findByUsuariOrderByIdDesc(Usuari) :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
        return rutesRepository.findByUsuariOrderByIdDesc(u);
    }
}
