package cat.copernic.amayo.frontend.usuariManagment.model

import cat.copernic.amayo.frontend.rutaManagment.model.Estat


data class Usuari (
    var email: String? = null,
    var nom: String? = null,
    var cognom: String? = null,
    var contra: String? = null,
    var telefon: String? = null,
    var poblacio: String? = null,
    var saldo: Double? = null,
    var rol: Rol? = null,
    var estat: Estat? = null,
    var foto: String? = null,
    var reserva: Boolean = false
)