package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.LocalDateTimeAdapter
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import com.google.gson.annotations.JsonAdapter
import java.time.LocalDateTime

/**
 * Representa una reserva d'una recompensa feta per un usuari.
 *
 * Aquesta classe conté tota la informació associada a la reserva,
 * incloent el seu estat, dates i relacions amb usuari i recompensa.
 *
 * @property id Identificador únic de la reserva.
 * @property caducada Indica si la reserva ha caducat.
 * @property datareserva Data en què es va realitzar la reserva.
 * @property estat Estat actual de la reserva.
 * @property emailUsuari Usuari que ha fet la reserva.
 * @property idRecompensa Recompensa associada a la reserva.
 */
data class Reserva(
    var id: Long? = null,
    var caducada: Boolean? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var datareserva: LocalDateTime? = null,
    var estat: EstatReserva? = null,
    var emailUsuari: Usuari? = null,
    var idRecompensa: Recompensa? = null
)