package cat.copernic.amayo.frontend.recompensaManagment.model

import cat.copernic.amayo.frontend.puntBesacanviManagment.model.PuntBescanvi
import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.LocalDateTimeAdapter
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import com.google.gson.annotations.JsonAdapter
import java.time.LocalDateTime

/**
 * Representa una recompensa dins del sistema.
 *
 * Aquesta classe conté tota la informació associada a una recompensa,
 * incloent la seva descripció, cost, estat, dates importants i relacions amb altres entitats.
 *
 * @property id Identificador únic de la recompensa.
 * @property descripcio Text descriptiu de la recompensa.
 * @property cost Cost de la recompensa en punts.
 * @property dataCreacio Data en què es va crear la recompensa.
 * @property estat Estat actual de la recompensa.
 * @property observacions Notes o comentaris addicionals.
 * @property foto Imatge de la recompensa en format Base64.
 * @property puntBescanviId Punt de bescanvi associat a la recompensa.
 * @property usuariRecompensa Usuari al qual s'ha assignat la recompensa, si escau.
 * @property dataEntrega Data en què la recompensa ha estat recollida.
 * @property dataReserva Data en què la recompensa ha estat reservada.
 * @property dataAsignacio Data en què la recompensa ha estat assignada a un usuari.
 */
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
