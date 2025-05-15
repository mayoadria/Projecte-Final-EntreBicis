package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.Session.SessionRepository
import cat.copernic.amayo.frontend.dataStore
import cat.copernic.amayo.frontend.rutaManagment.data.local.*
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitTLSInstance
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
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

    /* ---------- Dades per a la UI ---------- */
    private val _routeSegments = mutableStateListOf<MutableList<GeoPoint>>()
    val routeSegments: List<MutableList<GeoPoint>> get() = _routeSegments

    var isRouting by mutableStateOf(false);     private set
    private var currentRouteId: Long? = null

    var nomRuta  : String = "Mi ruta"
    var descRuta : String = "Descripción de ejemplo"

    /* ---------- ACUMULADORS DE MÈTRIQUES ---------- */
    private var startTimeMillis      : Long   = 0L
    private var totalDistanceMeters  : Double = 0.0
    private var totalStoppedMillis   : Long   = 0L
    private var velMax               : Double = 0.0
    private var prevPoint            : GeoPoint? = null
    private var prevTimestampMillis  : Long   = 0L

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

        /* --- reiniciem acumuladors --- */
        startTimeMillis     = System.currentTimeMillis()
        totalDistanceMeters = 0.0
        totalStoppedMillis  = 0L
        velMax              = 0.0
        prevPoint           = null
        prevTimestampMillis = 0L

        /* --- persistim ruta buida a Room perquè hi pengin les posicions --- */
        viewModelScope.launch {
            val newId = dao.insertRoute(RouteEntity(name = name, description = desc))
            currentRouteId = newId

            initialLocation?.let {
                recordPoint(it)                 // també l’emmagatzema a Room
            }
        }
    }

    /* ====================================================================== */
    /* ============================== NOU PUNT ============================== */
    /* ====================================================================== */
    fun addPoint(point: GeoPoint) {
        if (!isRouting) return
        recordPoint(point)                      // Desa i actualitza mètriques
    }

    /**
     * Desa la posició a Room i actualitza estadístiques en temps real.
     */
    private fun recordPoint(p: GeoPoint) {
        val rid = currentRouteId ?: return
        val now = System.currentTimeMillis()

        /* ---------- persistim a Room (coroutine IO) ---------- */
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

        /* ---------- actualitzem traça per a la UI ---------- */
        if (_routeSegments.isEmpty()) _routeSegments.add(mutableListOf())
        _routeSegments.last().add(p)

        /* ---------- mètriques ---------- */
        prevPoint?.let { prev ->
            val dtMillis = now - prevTimestampMillis
            if (dtMillis > 0) {
                val segmentMeters = haversineMeters(prev, p)
                val segmentKm     = segmentMeters / 1_000.0
                totalDistanceMeters += segmentMeters

                val velInstKmh = (segmentKm * 3_600_000.0) / dtMillis
                velMax = max(velMax, velInstKmh)

                if (velInstKmh < 1.0) {        // umbral “parat”
                    totalStoppedMillis += dtMillis
                }
            }
        }
        prevPoint           = p
        prevTimestampMillis = now
    }

    /* ====================================================================== */
    /* ============================ ATURAR RUTA ============================= */
    /* ====================================================================== */

    fun stopRoute() {
        if (!isRouting) return
        isRouting = false

        /* ---------- càlcul final ---------- */
        val endTimeMillis   = System.currentTimeMillis()
        val totalTimeMillis = endTimeMillis - startTimeMillis
        val movingMillis    = max(0L, totalTimeMillis - totalStoppedMillis)

        val distKm          = totalDistanceMeters / 1_000.0
        val velMitjaKmh     = if (movingMillis > 0)
            (distKm * 3_600_000.0) / movingMillis else 0.0

        val ritmeMinKm      = if (distKm > 0)
            (movingMillis / 60_000.0) / distKm else 0.0

        /* ---------- enviem al backend ---------- */
        viewModelScope.launch {
            currentRouteId?.let { rid ->
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

                val dto = RutaApi.RutaDto(
                    id            = null,
                    nom           = nomRuta,
                    descripcio    = descRuta,
                    estat         = RutaApi.EstatRutes.INVALIDA,
                    cicloRuta     = RutaApi.CicloRuta.FINALITZADA,
                    km            = distKm,
                    temps         = (totalTimeMillis / 1_000).toInt(),
                    tempsParat    = (totalStoppedMillis / 1_000).toInt(),
                    velMax        = velMax,
                    velMitja      = velMitjaKmh,
                    velMitjaKm    = ritmeMinKm,
                    posicions     = posDtos,
                    emailUsuari   = email
                )

                try {
                    val resp = rutaApi.saveRuta(dto)
                    if (!resp.isSuccessful) {
                        println("Error HTTP ${resp.code()}: ${resp.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("Excepció en enviar la ruta: $e")
                }

                /* ----- netegem BD local ----- */
                dao.deletePositionsForRoute(rid)
                dao.deleteRoute(rid)
                currentRouteId = null
            }
        }

        /* ---------- netegem UI ---------- */
        _routeSegments.clear()
    }

    /**
     * Distància Haversine entre dos GeoPoint en metres.
     */
    private fun haversineMeters(a: GeoPoint, b: GeoPoint): Double {
        val R = 6_371_000.0                    // radi terrestre (m)
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
