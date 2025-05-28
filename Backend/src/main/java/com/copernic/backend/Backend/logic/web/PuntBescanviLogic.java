package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.PuntBescanvi;
import com.copernic.backend.Backend.repository.BescanviRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servei de lògica de negoci per gestionar els punts de bescanvi.
 * Proporciona operacions per llistar, consultar, crear, modificar i eliminar punts de bescanvi,
 * encapsulant la interacció amb el repositori {@link BescanviRepository}.
 */
@Service
public class PuntBescanviLogic {

    /**
     * Repositori per a l'accés a dades de {@link PuntBescanvi}.
     */
    @Autowired
    private BescanviRepository bescanviRepository;

    /**
     * Llista tots els punts de bescanvi registrats al sistema.
     *
     * @return Llista de {@link PuntBescanvi}.
     */
    public List<PuntBescanvi> llistarBescanvi() {
        return bescanviRepository.findAll();
    }

    /**
     * Cerca un punt de bescanvi pel seu identificador.
     *
     * @param id Identificador del punt de bescanvi.
     * @return Punt de bescanvi trobat.
     * @throws java.util.NoSuchElementException si no es troba cap element amb l'ID especificat.
     */
    public PuntBescanvi findByID(Long id) {
        return bescanviRepository.findById(id).get();
    }

    /**
     * Desa un nou punt de bescanvi o actualitza'n un existent.
     *
     * @param bescanvi Objecte {@link PuntBescanvi} a desar.
     */
    public void guardarComerc(PuntBescanvi bescanvi) {
        bescanviRepository.save(bescanvi);
    }

    /**
     * Elimina un punt de bescanvi a partir del seu identificador.
     *
     * @param id Identificador del punt de bescanvi a eliminar.
     * @throws EntityNotFoundException si el punt no existeix.
     */
    public void eliminarBescanvi(Long id) {
        PuntBescanvi puntBescanvi = findByID(id);
        if (puntBescanvi == null) {
            throw new EntityNotFoundException("Punto de bescanvi no existe: " + id);
        }
        // Si hay integridad referencial (FK) u otro fallo, propaga la excepción
        bescanviRepository.delete(puntBescanvi);
    }
    /**
     * Actualitza la informació d'un punt de bescanvi existent.
     *
     * @param bescanvi Objecte {@link PuntBescanvi} amb les dades modificades.
     */
    public void modificarRecompensa(PuntBescanvi bescanvi) {
        bescanviRepository.save(bescanvi);
    }
}
