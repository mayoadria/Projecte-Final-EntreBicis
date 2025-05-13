package cat.copernic.amayo.frontend.recompensaManagment.model

/**
 * Enum que representa els possibles estats d'una recompensa.
 *
 * Defineix les diferents fases o situacions en què pot trobar-se una recompensa
 * dins del sistema de gestió de reserves i bescanvis.
 */
enum class Estat {

    /** La recompensa està disponible per ser reservada. */
    DISPONIBLES,

    /** La recompensa ha estat reservada per un usuari. */
    RESERVADES,

    /** La recompensa ha estat assignada a un usuari. */
    ASSIGNADES,

    /** La recompensa ha estat recollida per l'usuari. */
    RECOLLIDA,

    /** La recompensa està pendent de ser recollida. */
    PER_RECOLLIR
}