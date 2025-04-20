package cat.copernic.amayo.frontend.usuariManagment.data.remote

import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuariApi {
    @GET("validar/{email}/{contra}")
    suspend fun login(@Path("email") email : String, @Path("contra") contra : String): Response<Usuari>

    @GET("getByEmail/{email}")
    suspend fun getByEmail(@Path("email") email: String): Response<Usuari>

    @PUT("update")
    suspend fun update(@Body product: Usuari): Response<Void>

    @POST("sendEmail/{email}")
    suspend fun sendEmail(@Path("email") email : String): Response<Void>

    @GET("validateToken/{token}/{email}")
    suspend fun validateToken(@Path("token") token : String, @Path("email") email : String ): Response<Boolean>
}
