package cat.copernic.mycards.mycards_frontend.user_management.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que proporciona una instància de Retrofit per accedir a l'API de reserves.
 *
 * Aquesta instància està configurada per connectar-se a un servidor local
 * mitjançant la IP especial 10.0.2.2 (equivalent al localhost de l'ordinador des de l'emulador d'Android).
 *
 * Funcionalitats:
 * - Defineix la URL base de l'API de reserves.
 * - Configura el convertidor JSON amb GsonConverterFactory.
 * - Inicia la instància de Retrofit de forma lazy (només quan s'accedeix).
 *
 * @property retrofitInstance Instància única de Retrofit per a les operacions de reserva.
 */
object ReservaRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/api/reserva/"

    /**
     * Instància de Retrofit configurada amb la URL base i el convertidor Gson.
     * Es crea la primera vegada que s'accedeix i es reutilitza després.
     */
    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}