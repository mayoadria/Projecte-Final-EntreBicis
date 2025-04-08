package cat.copernic.amayo.frontend.usuariManagment.data.remote

import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuariApi {
    @GET("api/usuari/validar/{email}/{contra}")
    suspend fun login(@Path("email") email : String, @Path("contra") contra : String): Response<Usuari>

    @GET("api/usuari/getByEmail/{email}")
    suspend fun getByEmail(@Path("email") email: String): Response<Usuari>
}
