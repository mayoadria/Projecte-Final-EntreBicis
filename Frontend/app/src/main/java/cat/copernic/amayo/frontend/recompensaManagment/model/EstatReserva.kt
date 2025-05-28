package cat.copernic.amayo.frontend.recompensaManagment.model

/**
 * Enum que representa els diferents estats possibles d'una reserva.
 *
 * Permet identificar en quina fase del procés es troba una reserva dins del sistema.
 */
enum class EstatReserva {

    /** La reserva ha estat creada i està pendent de ser assignada o recollida. */
    RESERVADA,

    /** La reserva ha estat recollida per l'usuari. */
    RECOLLIDA,

    /** La reserva ha estat cancel·lada. */
    CANCELADA,

    /** La reserva ha estat assignada a una recompensa i usuari. */
    ASSIGNADA,

    /** La reserva ha estat desassignada i retorna a l'estat inicial. */
    DESASSIGNADA,

    /** La reserva ha caducat per no haver estat recollida a temps. */
    CADUCADA,

    /** La reserva està pendent de ser recollida pel seu destinatari. */
    PER_RECOLLIR
}
