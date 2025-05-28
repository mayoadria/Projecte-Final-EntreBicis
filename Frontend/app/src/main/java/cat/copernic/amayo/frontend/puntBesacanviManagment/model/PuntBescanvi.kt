package cat.copernic.amayo.frontend.puntBesacanviManagment.model

/**
 * Representa un punt de bescanvi dins del sistema.
 *
 * Aquesta classe conté la informació associada a un punt físic on es poden bescanviar recompenses.
 * Inclou dades de contacte, localització i una imatge representativa en format Base64.
 *
 * @property id Identificador únic del punt de bescanvi.
 * @property nom Nom del punt de bescanvi.
 * @property codiPostal Codi postal de la ubicació.
 * @property adreca Adreça física del punt de bescanvi.
 * @property telefon Telèfon de contacte del punt.
 * @property personaContacte Persona de referència per a contactes.
 * @property email Correu electrònic de contacte.
 * @property observacions Notes o observacions addicionals.
 * @property foto Imatge del punt en format Base64 (opcional).
 */
data class PuntBescanvi (
    var id: Long? = null,
    var nom: String? = null,
    var codiPostal: String? = null,
    var adreca: String? = null,
    var telefon: String? = null,
    var personaContacte: String? = null,
    var email:String? = null,
    var observacions: String? = null,
    var foto: String? = null
)