package cat.copernic.amayo.frontend.usuariManagment.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que proporciona una instancia de Retrofit para acceder a la API de usuarios.
 * Utiliza la URL base definida para realizar peticiones HTTP a los endpoints relacionados con usuarios.
 */
object UsuariRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/api/usuari/"

    /**
     * Instancia de Retrofit inicializada de forma perezosa.
     * Se crea al primer acceso y reutiliza la misma instancia para posteriores llamadas.
     */
    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(UsuariRetrofitInstance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
