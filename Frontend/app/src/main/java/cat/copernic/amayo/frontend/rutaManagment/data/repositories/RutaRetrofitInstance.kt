package cat.copernic.amayo.frontend.rutaManagment.data.repositories

import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RutaRetrofitInstance {
    // Apunta al prefijo /api/ directamente
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Gson lenient para no romper con JSON “casi válidos”
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Primero aceptamos respuestas de texto plano si las hay
            .addConverterFactory(ScalarsConverterFactory.create())
            // Luego el JSON normal con Gson
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: RutaApi by lazy {
        retrofit.create(RutaApi::class.java)
    }
}
