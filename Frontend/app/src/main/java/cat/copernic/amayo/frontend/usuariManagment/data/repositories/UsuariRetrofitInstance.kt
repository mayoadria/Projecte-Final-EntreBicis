package cat.copernic.amayo.frontend.usuariManagment.data.repositories

import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuariRetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/api/usuari/"

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(UsuariRetrofitInstance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
