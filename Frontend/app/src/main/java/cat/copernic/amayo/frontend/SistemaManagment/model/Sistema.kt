package cat.copernic.amayo.frontend.usuariManagment.model

import cat.copernic.amayo.frontend.puntBesacanviManagment.model.Estat

data class Sistema (
    var id: Long? = null,
    var velMax: Double? = null,
    var tempsMaxAturat: Double? = null,
    var Conversiosaldo: Int? = null,
    var tempsRecollida: String? = null,
)