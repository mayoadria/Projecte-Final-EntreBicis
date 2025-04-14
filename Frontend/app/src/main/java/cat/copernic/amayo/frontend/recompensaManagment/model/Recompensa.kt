package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.puntBesacanviManagment.model.PuntBescanvi
import cat.copernic.amayo.frontend.rutaManagment.model.Estat
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari

data class Recompensa(
    var id: Long? = null,
    var descripcio: String? = null,
    var cost: Int? = null,
    var estat: EstatRecompensa? = null,
    var observacions: String? = null,
    var foto: String? = null,
    var puntBescanviId: PuntBescanvi? = null,
    var usuariRecompensa: Usuari? = null
)
