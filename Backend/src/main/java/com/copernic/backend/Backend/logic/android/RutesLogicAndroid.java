package com.copernic.backend.Backend.logic.android;

import java.util.List;
import java.util.Optional;

import com.copernic.backend.Backend.logic.web.UsuariLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.repository.RutesRepository;

@Service
public class RutesLogicAndroid {

    @Autowired
    private RutesRepository rutesRepository;

    @Autowired
    private UsuariLogic usuariLogic;

    /** Guarda una ruta (ya existente) */
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
