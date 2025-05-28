package cat.copernic.amayo.frontend.Session

/**
 * Representa les dades de sessió d'un usuari.
 *
 * @property email Correu electrònic de l'usuari.
 * @property isConnected Indica si l'usuari està connectat o no.
 */
data class SessionUser(
    val email: String,
    val isConnected: Boolean,
)
