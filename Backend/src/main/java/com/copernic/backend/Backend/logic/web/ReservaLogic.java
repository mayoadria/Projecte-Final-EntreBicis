package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * Servei de lògica de negoci per gestionar les reserves.
 * Proporciona operacions per crear, consultar, modificar i eliminar reserves,
 * encapsulant la interacció amb el repositori {@link ReservaRepository}.
 */
@Service
public class ReservaLogic {
    /**
     * Repositori per a l'accés a dades de {@link Reserva}.
     */
    @Autowired
    private ReservaRepository reservaRepository;
    /**
     * Desa una nova reserva o actualitza'n una existent.
     *
     * @param reserva Objecte {@link Reserva} a desar.
     * @return L'identificador (ID) de la reserva desada.
     */
    public Long saveReserva(Reserva reserva) {
        Reserva ret = reservaRepository.save(reserva);

        return ret.getId();
    }
    /**
     * Llista totes les reserves registrades al sistema.
     *
     * @return Llista de {@link Reserva}.
     */
    public List<Reserva> llistarReserva() {
        return reservaRepository.findAll();
    }
    /**
     * Busca una reserva pel seu identificador.
     *
     * @param id Identificador de la reserva.
     * @return Objecte {@link Reserva} trobat, o {@code null} si no existeix.
     */
    public Reserva findById(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }
    /**
     * Actualitza la informació d'una reserva existent.
     * <p>
     * El camp identificador (email de l'usuari) no es modifica.
     * </p>
     *
     * @param usuariActualitzat Objecte {@link Reserva} amb les dades modificades.
     */
    public void updateReserva(Reserva usuariActualitzat) {
        // No se toca el campo "email" ya que es el identificador
        reservaRepository.save(usuariActualitzat);

    }
    /**
     * Comprova si existeix una reserva amb l'identificador especificat.
     *
     * @param id Identificador de la reserva.
     * @return {@code true} si existeix, {@code false} si no.
     */
    public boolean existsById(Long id)
    {
        Reserva p = reservaRepository.findById(id).orElse(null);

        return (p != null);
    }
    /**
     * Elimina una reserva pel seu identificador.
     *
     * @param id Identificador de la reserva a eliminar.
     */
    public void deleteReservaById(Long id){

        reservaRepository.deleteById(id);

    }
}
