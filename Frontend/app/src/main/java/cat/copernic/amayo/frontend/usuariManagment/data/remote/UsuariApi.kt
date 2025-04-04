package cat.copernic.amayo.frontend.usuariManagment.data.remote

import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuariApi {
    @POST("api/usuari/login")
    suspend fun login(@Body usuari: Usuari): Response<Usuari>
}
