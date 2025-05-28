package cat.copernic.mycards.mycards_frontend.user_management.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que proporciona una instància de [Retrofit] configurada per accedir a l'API de recompenses.
 *
 * Utilitza la IP `10.0.2.2` per connectar amb el backend local des de l'emulador d'Android.
 * Configura el convertidor JSON mitjançant [GsonConverterFactory].
 *
 * @property retrofitInstance Instància única de [Retrofit] inicialitzada de forma lazy.
 * Aquesta instància s'utilitza per fer crides a l'API de recompenses.
 */
object RecompensaRetrofitInstance {

    /** URL base de l'API de recompenses per a entorns locals (emulador Android). */
    private const val BASE_URL = "http://10.0.2.2:8080/api/recompensa/"

    /**
     * Instància de Retrofit configurada amb la URL base i el convertidor Gson.
     * Es crea només una vegada quan s'accedeix per primera vegada.
     */
    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}