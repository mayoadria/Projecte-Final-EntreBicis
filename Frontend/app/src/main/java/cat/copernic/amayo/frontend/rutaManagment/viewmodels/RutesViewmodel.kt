package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.Logger
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitTLSInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Operator { GREATER, LESS }

class RutesViewmodel(app: Application) : AndroidViewModel(app) {

    private val api = RutaRetrofitTLSInstance
        .retrofitTLSInstance
        .create(RutaApi::class.java)

    private var _allRoutes = listOf<RutaApi.RutaDto>()
    private val _routes = mutableStateListOf<RutaApi.RutaDto>()
    val routes: List<RutaApi.RutaDto> get() = _routes

    var filtroEstado by mutableStateOf<RutaApi.EstatRutes?>(null)
    var filtroFechaDesde by mutableStateOf<LocalDate?>(null)
    var filtroFechaHasta by mutableStateOf<LocalDate?>(null)
    var filtroKmRange by mutableStateOf(0f..50f)
    var filtroTimeRange by mutableStateOf(0f..5f)
    var filtroVelMedia by mutableStateOf<Pair<Operator, Float>?>(null)

    fun loadRoutes(email: String) {
        viewModelScope.launch {
            Logger.guardarLog(getApplication(), "Iniciant càrrega de rutes per a: $email")
            val response = api.getUserRoutes(email)

            if (response.isSuccessful) {
                _allRoutes = response.body().orEmpty()
                Logger.guardarLog(
                    getApplication(),
                    "S'han carregat ${_allRoutes.size} rutes per a $email"
                )
                api.getUserRoutes(email).takeIf { it.isSuccessful }?.body()?.let {
                    _allRoutes = it
                    applyFilters()
                }
            }else {
                Logger.guardarLog(
                    getApplication(),
                    "Error carregant rutes per a $email: HTTP ${response.code()}"
                )
            }
        }
    }

    fun applyFilters() {
        val maxKm = filtroKmRange.endInclusive
        val maxHrs = filtroTimeRange.endInclusive

        _routes.clear()
        _routes.addAll(_allRoutes.filter { ruta ->
            matchesEstado(ruta)
                    && matchesFecha(ruta)
                    && matchesKm(ruta, maxKm)
                    && matchesTiempo(ruta, maxHrs)
                    && matchesVelMedia(ruta)
        })
        Logger.guardarLog(
            getApplication(),
            "Aplicats filtres: estat=${filtroEstado}, km=${filtroKmRange}, temps=${filtroTimeRange}, vel=${filtroVelMedia} → ${_routes.size} resultats"
        )
    }

    private fun matchesEstado(r: RutaApi.RutaDto) =
        filtroEstado?.let { it == r.estat } ?: true

    private fun matchesFecha(r: RutaApi.RutaDto): Boolean {
        if (filtroFechaDesde == null && filtroFechaHasta == null) return true

        // extrae solo la parte de fecha: antes de 'T' o del espacio
        val raw = r.fechaCreacion
            .substringBefore('T')
            .substringBefore(' ')
        // intenta ISO, si falla prueba dd-MM-yyyy
        val date = runCatching { LocalDate.parse(raw) }
            .recoverCatching {
                LocalDate.parse(raw, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            }
            .getOrNull() ?: return false

        filtroFechaDesde?.let { if (date.isBefore(it)) return false }
        filtroFechaHasta?.let { if (date.isAfter(it)) return false }
        return true
    }

    private fun matchesKm(r: RutaApi.RutaDto, maxKm: Float): Boolean {
        val km = r.km.toFloat()
        return km >= filtroKmRange.start && (maxKm >= 50f || km <= maxKm)
    }

    private fun matchesTiempo(r: RutaApi.RutaDto, maxHrs: Float): Boolean {
        val hrs = r.temps / 3600f
        return hrs >= filtroTimeRange.start && (maxHrs >= 5f || hrs <= maxHrs)
    }

    private fun matchesVelMedia(r: RutaApi.RutaDto) =
        filtroVelMedia?.let { (op, lim) ->
            if (op == Operator.GREATER) r.velMitja >= lim
            else r.velMitja <= lim
        } ?: true

    /** Restablece todos los filtros y refresca la lista */
    fun clearFilters() {
        filtroEstado = null
        filtroFechaDesde = null
        filtroFechaHasta = null
        filtroKmRange = 0f..50f
        filtroTimeRange = 0f..5f
        filtroVelMedia = null
        Logger.guardarLog(getApplication(), "Filtres reiniciats")
        applyFilters()
    }
}

