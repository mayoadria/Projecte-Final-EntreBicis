package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitTLSInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Operator { GREATER, LESS }
enum class SortField { DATE, KM, TIME, SPEED }
enum class SortOrder { ASC, DESC }

class RutesViewmodel(app: Application) : AndroidViewModel(app) {

    private val api = RutaRetrofitTLSInstance
        .retrofitTLSInstance
        .create(RutaApi::class.java)

    private var _allRoutes = listOf<RutaApi.RutaDto>()
    private val _routes = mutableStateListOf<RutaApi.RutaDto>()
    val routes: List<RutaApi.RutaDto> get() = _routes

    var filtroEstado     by mutableStateOf<RutaApi.EstatRutes?>(null)
    var filtroFechaDesde by mutableStateOf<LocalDate?>(null)
    var filtroFechaHasta by mutableStateOf<LocalDate?>(null)
    var filtroKmRange    by mutableStateOf(0f..50f)
    var filtroTimeRange  by mutableStateOf(0f..5f)
    var filtroVelMedia   by mutableStateOf<Pair<Operator, Float>?>(null)
    var sortField by mutableStateOf<SortField?>(null)
    var sortOrder by mutableStateOf<SortOrder?>(null)


    fun loadRoutes(email: String) {
        viewModelScope.launch {
            api.getUserRoutes(email).takeIf { it.isSuccessful }?.body()?.let {
                _allRoutes = it
                applyFilters()
            }
        }
    }

    fun applyFilters() {
        val maxKm  = filtroKmRange.endInclusive
        val maxHrs = filtroTimeRange.endInclusive

        _routes.clear()
        _routes.addAll(_allRoutes.filter { ruta ->
            matchesEstado(ruta)
                    && matchesFecha(ruta)
                    && matchesKm(ruta, maxKm)
                    && matchesTiempo(ruta, maxHrs)
                    && matchesVelMedia(ruta)
        })
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
        filtroFechaHasta?.let { if (date.isAfter(it))  return false }
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
            else                                  r.velMitja <= lim
        } ?: true
}
