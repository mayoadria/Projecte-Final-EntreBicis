package cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote

import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RecompensasApiRest {

    @GET("all")
    suspend fun findAll(): Response<List<Recompensa>>

    @GET("byId/{id}")
    suspend fun getById(@Path("id") id : Long ): Response<Recompensa>

}