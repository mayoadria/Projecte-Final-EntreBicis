package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa els diferents estats d'una reserva dins del sistema.
 * Controla el cicle de vida d'una reserva des de la seva creació fins a la seva finalització o cancel·lació.
 */
public enum EstatReserva {

    /**
     * La reserva ha estat creada i assignada a una recompensa però encara no ha estat recollida.
     */
    RESERVADA,

    /**
     * La reserva ha estat completada amb èxit i la recompensa ha estat recollida per l'usuari.
     */
    RECOLLIDA,

    /**
     * La reserva ha estat cancel·lada abans de ser recollida.
     */
    CANCELADA,

    /**
     * La reserva ha estat formalment assignada a l'usuari per a la seva recollida.
     */
    ASSIGNADA,

    /**
     * La reserva ha estat desassignada, deixant la recompensa disponible de nou.
     */
    DESASSIGNADA,

    /**
     * La reserva ha caducat en no ser recollida dins del termini establert.
     */
    CADUCADA,

    /**
     * La reserva està pendent de recollir però ja ha estat gestionada.
     */
    PER_RECOLLIR
}

