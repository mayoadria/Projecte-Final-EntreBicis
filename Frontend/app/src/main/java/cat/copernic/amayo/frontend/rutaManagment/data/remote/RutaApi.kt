package cat.copernic.amayo.frontend.rutaManagment.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RutaApi {

    @POST("ruta")
    suspend fun saveRuta(@Body rutaDto: RutaDto): Response<RutaDto>

    data class PosicioDto(
        val latitud: Double,
        val longitud: Double,
        val temps: Int
    )

    // DTO principal con admin + ciclo + posiciones
    enum class EstatRutes { VALIDA, INVALIDA }
    enum class CicloRuta  { INICIADA, PAUSADA, FINALITZADA }

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
        val emailUsuari:  String
    )
}
