package cat.copernic.amayo.frontend.rutaManagment.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RutaApi {

    @POST("ruta/{rutaId}/posicions")
    suspend fun savePosicio(
        @Path("rutaId") rutaId: Long,
        @Body posicioDto: PosicioDto
    ): Response<PosicioGpsResponse>

    data class PosicioDto(
        val latitud: Double,
        val longitud: Double,
        val temps: Int
    )

    data class PosicioGpsResponse(
        val id: Long,
        val latitud: Double,
        val longitud: Double,
        val temps: Int
    )
}
