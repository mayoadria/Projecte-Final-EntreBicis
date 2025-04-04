package cat.copernic.amayo.frontend.usuariManagment.data.repositories

import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuariRetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: UsuariApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuariApi::class.java)
    }
}
