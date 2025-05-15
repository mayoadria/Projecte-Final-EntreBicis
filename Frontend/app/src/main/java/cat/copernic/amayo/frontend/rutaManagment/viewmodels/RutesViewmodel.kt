package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitTLSInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Operador para filtro de velocidad media */
enum class Operator { GREATER, LESS }

class RutesViewmodel(app: Application) : AndroidViewModel(app) {

    private val api = RutaRetrofitTLSInstance
        .retrofitTLSInstance
        .create(RutaApi::class.java)

    // 1) Todas las rutas bajadas
    private var _allRoutes = listOf<RutaApi.RutaDto>()

    // 2) Rutas filtradas que expongo a la UI
    private val _routes = mutableStateListOf<RutaApi.RutaDto>()
    val routes: List<RutaApi.RutaDto> get() = _routes

    // ─── Estados de filtro ───────────────────────────────────────────────
    var filtroEstado     by mutableStateOf<RutaApi.EstatRutes?>(null)
    var filtroFechaDesde by mutableStateOf<LocalDate?>(null)
    var filtroFechaHasta by mutableStateOf<LocalDate?>(null)
    var filtroKmRange    by mutableStateOf(0f..50f)   // 50f = sin límite
    var filtroTimeRange  by mutableStateOf(0f..5f)    // 5h = sin límite
    var filtroVelMedia   by mutableStateOf<Pair<Operator, Float>?>(null)

    /** Carga las rutas del servidor y aplica filtros iniciales */
    fun loadRoutes(email: String) {
        viewModelScope.launch {
            api.getUserRoutes(email).takeIf { it.isSuccessful }?.body()?.let { list ->
                _allRoutes = list
                applyFilters()
            }
        }
    }

    /** Vuelve a calcular `_routes` a partir de `_allRoutes` y los criterios */
    fun applyFilters() {
        val maxKm    = filtroKmRange.endInclusive
        val maxHrs   = filtroTimeRange.endInclusive

        _routes.clear()
        _routes.addAll(_allRoutes.filter { ruta ->
            matchesEstado(ruta)
                    && matchesFecha(ruta)
                    && matchesKm(ruta, maxKm)
                    && matchesTiempo(ruta, maxHrs)
                    && matchesVelMedia(ruta)
        })
    }

    private fun matchesEstado(r: RutaApi.RutaDto): Boolean =
        filtroEstado?.let { it == r.estat } ?: true

    private fun matchesFecha(r: RutaApi.RutaDto): Boolean {
        if (filtroFechaDesde == null && filtroFechaHasta == null) return true

        // 1) cortamos hasta la T (si existe), y luego hasta el espacio (si existe)
        val rawDate = r.fechaCreacion
            .substringBefore('T')     // convierte "2025-05-13T14:30:00" → "2025-05-13"
            .substringBefore(' ')     // o convierte "2025-05-13 14:30:00" → "2025-05-13"

        // 2) parseamos protegidos contra fallo
        val date = runCatching { LocalDate.parse(rawDate) }
            .getOrNull() ?: return false

        filtroFechaDesde?.let { if (date.isBefore(it)) return false }
        filtroFechaHasta?.let { if (date.isAfter(it))  return false }
        return true
    }





    private fun matchesKm(r: RutaApi.RutaDto, maxKm: Float): Boolean {
        val km = r.km.toFloat()
        return km >= filtroKmRange.start &&
                (maxKm >= 50f || km <= maxKm)
    }

    private fun matchesTiempo(r: RutaApi.RutaDto, maxHrs: Float): Boolean {
        val hrs = r.temps / 3600f
        return hrs >= filtroTimeRange.start &&
                (maxHrs >= 5f || hrs <= maxHrs)
    }

    private fun matchesVelMedia(r: RutaApi.RutaDto): Boolean =
        filtroVelMedia?.let { (op, lim) ->
            if (op == Operator.GREATER) r.velMitja >= lim
            else                                r.velMitja <= lim
        } ?: true

    /** Restablece todos los filtros y refresca la lista */
    fun clearFilters() {
        filtroEstado     = null
        filtroFechaDesde = null
        filtroFechaHasta = null
        filtroKmRange    = 0f..50f
        filtroTimeRange  = 0f..5f
        filtroVelMedia   = null
        applyFilters()
    }
}
