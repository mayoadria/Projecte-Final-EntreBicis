package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa les diferents fases del cicle de vida d'una ruta.
 * S'utilitza per identificar en quin estat es troba una ruta mentre es realitza o després de completar-la.
 */
public enum CicloRuta {

    /**
     * La ruta ha començat i està en curs.
     */
    INICIADA,

    /**
     * La ruta ha estat pausada temporalment.
     */
    PAUSADA,

    /**
     * La ruta ha finalitzat completament.
     */
    FINALITZADA
}