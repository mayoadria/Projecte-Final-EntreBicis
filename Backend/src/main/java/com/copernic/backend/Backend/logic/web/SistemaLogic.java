package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.repository.SistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Servei de lògica de negoci per gestionar la configuració general del sistema.
 * Aquesta classe encapsula les operacions per consultar i modificar la configuració global
 * emmagatzemada a la base de dades mitjançant el repositori {@link SistemaRepository}.
 */
@Service
public class SistemaLogic {

    /**
     * Repositori per a l'accés a dades de {@link Sistema}.
     */
    @Autowired
    private SistemaRepository sistemaRepository;

    /**
     * Obté la configuració actual del sistema.
     * Si no existeix un registre amb ID 1, es retorna un nou objecte {@link Sistema} amb valors per defecte.
     *
     * @return Instància de {@link Sistema} amb la configuració actual.
     */
    public Sistema getSistema() {
        return sistemaRepository.findById(1L)
                .orElse(new Sistema(1L, 0.0, Duration.ZERO, 0, Duration.ZERO));
    }

    /**
     * Desa o actualitza la configuració del sistema.
     *
     * @param sistema Objecte {@link Sistema} a desar.
     */
    public void guardarSistema(Sistema sistema) {
        sistemaRepository.save(sistema);
    }

    /**
     * Obté el valor del temps de recollida definit en la configuració del sistema.
     *
     * @return Durada màxima per recollir recompenses.
     */
    public Duration getTempsRecollida() {
        return getSistema().getTempsRecollida();
    }
}
