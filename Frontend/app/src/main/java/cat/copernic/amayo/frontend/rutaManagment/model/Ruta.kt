package cat.copernic.amayo.frontend.rutaManagment.model

import cat.copernic.amayo.frontend.puntBesacanviManagment.model.Estat

data class ruta(
    var id: Long? = null,
    var nom: String? = null,
    var descripcio: String? = null,
    var estat: Estat? = null,
    var velMedia: Double? = null,
    var velMax: Double? = null,
    var velMitjaKm: Double? = null,
    var tempsParat: Double? = null,
    var temps: String? = null,
)
