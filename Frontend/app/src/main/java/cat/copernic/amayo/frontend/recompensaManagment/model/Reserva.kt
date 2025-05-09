package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.LocalDateTimeAdapter
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import com.google.gson.annotations.JsonAdapter
import java.time.LocalDateTime

data class Reserva(
    var id: Long? = null,
    var caducada: Boolean? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var datareserva: LocalDateTime? = null,
    var estat: EstatReserva? = null,
    var emailUsuari: Usuari? = null,
    var idRecompensa: Recompensa? = null
)