package cat.copernic.mycards.mycards_frontend.user_management.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecompensaRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/api/recompensa/"

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}