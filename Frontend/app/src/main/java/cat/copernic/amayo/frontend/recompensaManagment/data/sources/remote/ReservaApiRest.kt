package cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote

import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReservaApiRest {

    @POST("crear")
    suspend fun save(@Body reserva: Reserva): Response<Long>

    @GET("all")
    suspend fun findAll(): Response<List<Reserva>>
    @GET("byId/{id}")
    suspend fun getById(@Path("id") id : Long ): Response<Reserva>

    @PUT("update")
    suspend fun update(@Body product: Reserva): Response<Void>

}