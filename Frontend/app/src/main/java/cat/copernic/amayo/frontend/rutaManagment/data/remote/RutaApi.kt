package cat.copernic.amayo.frontend.rutaManagment.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz para acceder a la API de rutas.
 * Define las operaciones disponibles para enviar rutas al servidor.
 */
interface RutaApi {

    /**
     * Envía una nueva ruta al backend para ser guardada.
     *
     * @param rutaDto Los datos de la ruta a guardar.
     * @return Una respuesta que contiene el objeto RutaDto guardado.
     */
    @POST("ruta")
    suspend fun saveRuta(@Body rutaDto: RutaDto): Response<RutaDto>


    /** Listar rutas de un usuario */
    @GET("ruta/usuari/{email}")
    suspend fun getUserRoutes(
        @Path("email") email: String
    ): Response<List<RutaDto>>


    /**
     * DTO que representa una posición GPS con latitud, longitud y tiempo transcurrido.
     *
     * @property latitud Coordenada de latitud.
     * @property longitud Coordenada de longitud.
     * @property temps Tiempo transcurrido en segundos en esa posición.
     */
    data class PosicioDto(
        val latitud: Double,
        val longitud: Double,
        val temps: Int
    )

    /**
     * Enum que indica el estado de validación de una ruta.
     */
    enum class EstatRutes { VALIDA, INVALIDA }
    /**
     * Enum que representa el ciclo de vida de una ruta.
     */
    enum class CicloRuta  { INICIADA, PAUSADA, FINALITZADA }

    /**
     * DTO principal que contiene toda la información de una ruta,
     * incluyendo métricas, estado, ciclo, posiciones y el usuario asociado.
     *
     * @property id Identificador de la ruta (opcional).
     * @property nom Nombre de la ruta.
     * @property descripcio Descripción de la ruta.
     * @property estat Estado de validación de la ruta.
     * @property cicloRuta Ciclo de vida actual de la ruta.
     * @property km Distancia total de la ruta en kilómetros.
     * @property temps Tiempo total en segundos.
     * @property tempsParat Tiempo detenido en segundos (velocidad < 1 km/h).
     * @property velMax Velocidad máxima alcanzada (km/h).
     * @property velMitja Velocidad media en movimiento (km/h).
     * @property velMitjaKm Ritmo medio en min/km.
     * @property posicions Lista de posiciones GPS registradas durante la ruta.
     * @property emailUsuari Email del usuario propietario de la ruta.
     */
    data class RutaDto(
        val id:           Long?        = null,
        val nom:          String,
        val descripcio:   String,
        val estat:        EstatRutes,
        val cicloRuta:    CicloRuta,

        val km:           Double,
        val temps:        Int,         // segons totals
        val tempsParat:   Int,         // segons parat (vel < 1 km/h)
        val velMax:       Double,      // km/h
        val velMitja:     Double,      // km/h (només en moviment)
        val velMitjaKm:   Double,      // min/km (ritme)

        val posicions:    List<PosicioDto>,
        val emailUsuari:  String,
        val fechaCreacion: String = "",
        val punts: Double
    )
}
