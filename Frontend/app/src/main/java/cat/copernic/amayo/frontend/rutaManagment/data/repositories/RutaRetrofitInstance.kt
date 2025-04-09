package cat.copernic.amayo.frontend.rutaManagment.data.repositories

import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RutaRetrofitInstance {
    // Asegúrate de poner la URL correcta de tu backend
    private const val BASE_URL = "http://tu-backend-url:puerto/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RutaApi by lazy {
        retrofit.create(RutaApi::class.java)
    }
}
