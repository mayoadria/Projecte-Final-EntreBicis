package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.usuariManagment.model.Usuari

data class Reserva(
    var id: Long? = null,
    var Datareserva: String? = null,
    var estat: EstatReserva? = null,
    var emailUsuari: Usuari? = null,
    var idRecompensa: Recompensa? = null
)