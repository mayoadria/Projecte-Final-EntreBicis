package cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote

import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservaApiRest {

    @POST("crear")
    suspend fun save(@Body reserva: Reserva): Response<Long>


}