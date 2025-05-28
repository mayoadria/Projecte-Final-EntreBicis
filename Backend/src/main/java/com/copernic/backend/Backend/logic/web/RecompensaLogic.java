package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servei de lògica de negoci per gestionar les recompenses.
 * Proporciona operacions per crear, consultar, modificar i eliminar recompenses,
 * així com consultes específiques per punts de bescanvi i usuaris.
 */
@Service
public class RecompensaLogic {
    /**
     * Repositori per a l'accés a dades de {@link Recompensas}.
     */
    @Autowired
    private RecompensasRepository recompensasRepository;

    /**
     * Desa una nova recompensa o actualitza'n una existent.
     *
     * @param recompensa Objecte {@link Recompensas} a desar.
     */
    public void guardarRecompensa(Recompensas recompensa) {
        recompensasRepository.save(recompensa);
    }

    /**
     * Llista totes les recompenses registrades.
     *
     * @return Llista de {@link Recompensas}.
     */
    public List<Recompensas> llistarRecompensas() {
        return recompensasRepository.findAll();
    }

    /**
     * Llista totes les recompenses per a l'app Android.
     *
     * @return Llista de {@link Recompensas} per a dispositius Android.
     */
    public List<Recompensas> llistarRecompensasAndroid() {

        List<Recompensas> ret = new ArrayList<>();

        ret = recompensasRepository.findAll();

        return ret;

    }

    /**
     * Elimina una recompensa si el seu estat és {@link Estat#DISPONIBLES}.
     *
     * @param id Identificador de la recompensa a eliminar.
     * @return Missatge indicant l'estat de l'operació.
     */
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

    /**
     * Actualitza la informació d'una recompensa existent.
     *
     * @param recompensa Objecte {@link Recompensas} amb les dades actualitzades.
     */
    public void modificarRecompensa(Recompensas recompensa) {
        recompensasRepository.save(recompensa);
    }

    /**
     * Busca una recompensa pel seu identificador.
     *
     * @param id Identificador de la recompensa.
     * @return Objecte {@link Recompensas} trobat.
     * @throws java.util.NoSuchElementException si no es troba cap element amb l'ID especificat.
     */
    public Recompensas findById(Long id) {
        return recompensasRepository.findById(id).get();
    }

    /**
     * Comprova si existeix una recompensa amb el identificador proporcionat.
     *
     * @param email Identificador de la recompensa (confusió: potser hauria de ser idRecompensa, no email).
     * @return {@code true} si existeix, {@code false} si no.
     */
    public boolean existeRecompensa(Long email) {
        Recompensas usu = recompensasRepository.findById(email).orElse(null);
        return usu != null;
    }

    /**
     * Llista totes les recompenses associades a un punt de bescanvi.
     *
     * @param puntId Identificador del punt de bescanvi.
     * @return Llista de {@link Recompensas} associades al punt.
     */
    public List<Recompensas> llistarPerPunt(Long puntId) {
        return recompensasRepository.findByPuntBescanviId_Id(puntId);
    }

    /**
     * Llista totes les recompenses associades a un usuari pel seu email.
     *
     * @param email Correu electrònic de l'usuari.
     * @return Llista de {@link Recompensas} associades a l'usuari.
     */
    public List<Recompensas> llistarPerEmailUsuari(String email) {
        return recompensasRepository.findByUsuariRecompensa_Email(email);
    }


}
