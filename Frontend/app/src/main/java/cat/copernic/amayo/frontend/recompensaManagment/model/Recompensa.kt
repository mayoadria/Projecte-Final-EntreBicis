package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.puntBesacanviManagment.model.PuntBescanvi
import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.LocalDateTimeAdapter
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import com.google.gson.annotations.JsonAdapter
import java.time.LocalDateTime

data class Recompensa(
    var id: Long? = null,
    var descripcio: String? = null,
    var cost: Int? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var dataCreacio: LocalDateTime? = null,
    var estat: cat.copernic.amayo.frontend.recompensaManagment.model.Estat? = null,
    var observacions: String? = null,
    var foto: String? = null,
    var puntBescanviId: PuntBescanvi? = null,
    var usuariRecompensa: Usuari? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var dataEntrega: LocalDateTime? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var dataReserva: LocalDateTime? = null,
    @field:JsonAdapter(LocalDateTimeAdapter::class)
    var dataAsignacio: LocalDateTime? = null
)
