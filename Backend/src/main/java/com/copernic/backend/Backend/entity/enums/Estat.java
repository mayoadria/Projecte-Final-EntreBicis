package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa els diferents estats d'una recompensa dins del sistema.
 * S'utilitza per controlar el flux i disponibilitat de les recompenses en el procés de reserva i recollida.
 */
public enum Estat {

    /**
     * La recompensa està disponible per ser reservada.
     */
    DISPONIBLES,

    /**
     * La recompensa ha estat reservada per un usuari però encara no assignada.
     */
    RESERVADES,

    /**
     * La recompensa ha estat assignada a un usuari i està pendent de recollida.
     */
    ASSIGNADES,

    /**
     * La recompensa ja ha estat recollida per l'usuari.
     */
    RECOLLIDA,

    /**
     * La recompensa està pendent de recollir però ja ha estat reservada i gestionada.
     */
    PER_RECOLLIR
}

