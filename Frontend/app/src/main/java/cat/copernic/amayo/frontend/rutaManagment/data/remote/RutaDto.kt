package cat.copernic.amayo.frontend.rutaManagment.data.remote

/** Estado de validación de la ruta en el backend */
enum class EstatRutes { VALIDA, INVALIDA }
/** Ciclo de vida mientras se graba */
enum class CicloRuta  { INICIADA, PAUSADA, FINALITZADA }

data class PosicioDto(
    val latitud: Double,
    val longitud: Double,
    val temps: Int
)

/** DTO que enviamos al backend */
data class RutaDto(
    val id: Long? = null,
    val nom: String,
    val descripcio: String,
    val posicions: List<PosicioDto>,
    val estat: EstatRutes = EstatRutes.INVALIDA,
    val cicloRuta: CicloRuta = CicloRuta.FINALITZADA,
    val emailUsuari: String
)
