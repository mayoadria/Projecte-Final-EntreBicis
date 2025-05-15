package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.auth.SessionRepository
import cat.copernic.amayo.frontend.dataStore
import cat.copernic.amayo.frontend.rutaManagment.data.local.*
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitTLSInstance
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

class RutaViewModel(
    app: Application,
    private val sessionRepository: SessionRepository
) : AndroidViewModel(app) {

    /* ---------- Constructors ---------- */
    constructor(app: Application) : this(app, SessionRepository(app.dataStore))

    /* ---------- DAO & API ---------- */
    private val dao: RouteDao = AppDatabase.getInstance(app).routeDao()
    private val rutaApi: RutaApi =
        RutaRetrofitTLSInstance.retrofitTLSInstance.create(RutaApi::class.java)

    /* ---------- Datos para la UI ---------- */
    private val _routeSegments = mutableStateListOf<MutableList<GeoPoint>>()
    val routeSegments: List<MutableList<GeoPoint>> get() = _routeSegments

    var isRouting by mutableStateOf(false);     private set
    private var currentRouteId: Long? = null

    var nomRuta  : String = "Mi ruta"
    var descRuta : String = "Descripción de ejemplo"

    /* ---------- Acumuladores de métricas ---------- */
    private var startTimeMillis      : Long   = 0L
    private var totalDistanceMeters  : Double = 0.0
    private var totalStoppedMillis   : Long   = 0L
    private var velMax               : Double = 0.0
    private var prevPoint            : GeoPoint? = null
    private var prevTimestampMillis  : Long   = 0L
    private var punts: Double = 0.0

    /* ---------- Stats pendientes (para el popup) ---------- */
    data class RouteStats(
        val distKm:       Double,
        val totalMillis:  Long,
        val movingMillis: Long,
        val stoppedMillis: Long,
        val velMaxKmh:    Double,
        val velMedKmh:    Double,
        val ritmeMinKm:   Double
    )
    var pendingStats: RouteStats? by mutableStateOf(null); private set

    /* ====================================================================== */
    /* ============================ INICI RUTA ============================== */
    /* ====================================================================== */
    fun startRoute(name: String, desc: String, initialLocation: GeoPoint?) {
        nomRuta  = name
        descRuta = desc

        /* --- UI --- */
        isRouting = true
        _routeSegments.clear()
        _routeSegments.add(mutableListOf())

        /* --- reiniciamos acumuladores --- */
        startTimeMillis     = System.currentTimeMillis()
        totalDistanceMeters = 0.0
        totalStoppedMillis  = 0L
        velMax              = 0.0
        prevPoint           = null
        prevTimestampMillis = 0L

        /* --- persistimos ruta vacía a Room para colgar las posiciones --- */
        viewModelScope.launch {
            val newId = dao.insertRoute(RouteEntity(name = name, description = desc))
            currentRouteId = newId

            initialLocation?.let {
                recordPoint(it)                 // también la almacena en Room
            }
        }
    }

    /* ====================================================================== */
    /* ============================== NUEVO PUNTO =========================== */
    /* ====================================================================== */
    fun addPoint(point: GeoPoint) {
        if (!isRouting) return
        recordPoint(point)                      // Guarda y actualiza métricas
    }

    /**
     * Guarda la posición en Room y actualiza estadísticas en tiempo real.
     */
    private fun recordPoint(p: GeoPoint) {
        val rid = currentRouteId ?: return
        val now = System.currentTimeMillis()

        /* ---------- persistimos en Room (coroutine IO) ---------- */
        viewModelScope.launch {
            dao.insertPosition(
                PositionEntity(
                    routeId   = rid,
                    latitude  = p.latitude,
                    longitude = p.longitude,
                    timestamp = now
                )
            )
        }

        /* ---------- actualizamos traza para la UI ---------- */
        if (_routeSegments.isEmpty()) _routeSegments.add(mutableListOf())
        _routeSegments.last().add(p)

        /* ---------- métricas ---------- */
        prevPoint?.let { prev ->
            val dtMillis = now - prevTimestampMillis
            if (dtMillis > 0) {
                val segmentMeters = haversineMeters(prev, p)
                val segmentKm     = segmentMeters / 1_000.0
                totalDistanceMeters += segmentMeters

                val velInstKmh = (segmentKm * 3_600_000.0) / dtMillis
                velMax = max(velMax, velInstKmh)

                if (velInstKmh < 1.0) {        // umbral “parado”
                    totalStoppedMillis += dtMillis
                }
            }
        }
        prevPoint           = p
        prevTimestampMillis = now
    }

    /* ====================================================================== */
    /* ============================ PARAR RUTA ============================== */
    /* ====================================================================== */

    /**
     * Detiene la grabación y deja las estadísticas en `pendingStats`
     * para que la UI muestre el diálogo de resumen.
     */
    fun requestStop() {
        if (!isRouting) return
        isRouting = false
        pendingStats = computeStats()
    }

    /**
     * Guarda la ruta (lógica antigua) reutilizando las estadísticas calculadas.
     */
    fun saveRoute(name: String, desc: String) {
        val stats = pendingStats ?: return
        nomRuta  = name
        descRuta = desc
        pendingStats = null
        finalizeRoute(stats, sendToBackend = true)
    }

    /**
     * Descarta la ruta: borra BD local y limpia estado.
     */
    fun discardRoute() {
        val stats = pendingStats ?: return      // usamos para saber ID y limpiar
        pendingStats = null
        finalizeRoute(stats, sendToBackend = false)
    }

    /* ====================================================================== */
    /* ========================== CÁLCULO DE STATS ========================== */
    /* ====================================================================== */
    private fun computeStats(): RouteStats {
        val endTimeMillis   = System.currentTimeMillis()
        val totalTimeMillis = endTimeMillis - startTimeMillis
        val movingMillis    = max(0L, totalTimeMillis - totalStoppedMillis)

        val distKm      = totalDistanceMeters / 1_000.0
        val velMedKmh   = if (movingMillis > 0)
            (distKm * 3_600_000.0) / movingMillis else 0.0
        val ritmeMinKm  = if (distKm > 0)
            (movingMillis / 60_000.0) / distKm else 0.0

        return RouteStats(
            distKm         = distKm,
            totalMillis    = totalTimeMillis,
            movingMillis   = movingMillis,
            stoppedMillis  = totalStoppedMillis,
            velMaxKmh      = velMax,
            velMedKmh      = velMedKmh,
            ritmeMinKm     = ritmeMinKm
        )
    }

    /* ====================================================================== */
    /* ======================== FINALIZAR / LIMPIAR ========================= */
    /* ====================================================================== */
    private fun finalizeRoute(stats: RouteStats, sendToBackend: Boolean) {
        val rid = currentRouteId

        if (sendToBackend && rid != null) {
            /* ---------- enviamos al backend ---------- */
            viewModelScope.launch {
                val positions = dao.getPositionsForRoute(rid)
                val firstTs   = positions.firstOrNull()?.timestamp ?: startTimeMillis

                val posDtos = positions.map { p ->
                    RutaApi.PosicioDto(
                        latitud  = p.latitude,
                        longitud = p.longitude,
                        temps    = ((p.timestamp - firstTs) / 1_000).toInt()
                    )
                }

                val email = sessionRepository.getCurrentEmail()
                val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

                val dto = RutaApi.RutaDto(
                    id            = null,
                    nom           = nomRuta,
                    descripcio    = descRuta,
                    estat         = RutaApi.EstatRutes.INVALIDA,
                    cicloRuta     = RutaApi.CicloRuta.FINALITZADA,
                    km            = stats.distKm,
                    temps         = (stats.totalMillis / 1_000).toInt(),
                    tempsParat    = (stats.stoppedMillis / 1_000).toInt(),
                    velMax        = stats.velMaxKmh,
                    velMitja      = stats.velMedKmh,
                    velMitjaKm    = stats.ritmeMinKm,
                    posicions     = posDtos,
                    emailUsuari   = email,
                    fechaCreacion = now,
                    punts = punts
                )

                try {
                    val resp = rutaApi.saveRuta(dto)
                    if (!resp.isSuccessful) {
                        println("Error HTTP ${resp.code()}: ${resp.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("Excepción al enviar la ruta: $e")
                }

                /* ----- limpiamos BD local ----- */
                dao.deletePositionsForRoute(rid)
                dao.deleteRoute(rid)
            }
        } else if (rid != null) {
            /* ----- solo limpiamos BD local (descartar) ----- */
            viewModelScope.launch {
                dao.deletePositionsForRoute(rid)
                dao.deleteRoute(rid)
            }
        }

        /* ---------- limpiamos estado UI ---------- */
        currentRouteId = null
        _routeSegments.clear()
    }

    /**
     * Distancia Haversine entre dos GeoPoint en metros.
     */
    private fun haversineMeters(a: GeoPoint, b: GeoPoint): Double {
        val R = 6_371_000.0                    // radio terrestre (m)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)

        val h = sin(dLat / 2).pow(2.0) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2).pow(2.0)

        return 2 * R * asin(sqrt(h))
    }
}
