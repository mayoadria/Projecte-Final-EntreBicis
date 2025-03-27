package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.rutaManagment.model.Estat

data class Recompensa(
    var id: Long? = null,
    var descripcio: String? = null,
    var cost: Int? = null,
    var estat: Estat? = null,
    var observacions: String? = null,
    var image: String? = null,
)
