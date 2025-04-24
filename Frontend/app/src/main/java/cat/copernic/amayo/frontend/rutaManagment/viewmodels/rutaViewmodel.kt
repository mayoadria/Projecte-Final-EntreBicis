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
import cat.copernic.amayo.frontend.rutaManagment.data.local.AppDatabase
import cat.copernic.amayo.frontend.rutaManagment.data.local.PositionEntity
import cat.copernic.amayo.frontend.rutaManagment.data.local.RouteEntity
import cat.copernic.amayo.frontend.rutaManagment.data.local.RouteDao
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitInstance
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class RutaViewModel(
    app: Application,
    private val sessionRepository: SessionRepository
) : AndroidViewModel(app) {

    constructor(app: Application) : this(
        app,
        SessionRepository(app.dataStore)
    )

    private val dao: RouteDao = AppDatabase.getInstance(app).routeDao()

    // Lista de segmentos para dibujar en la UI
    private val _routeSegments = mutableStateListOf<MutableList<GeoPoint>>()
    val routeSegments: List<MutableList<GeoPoint>> get() = _routeSegments

    private var currentRouteId: Long? = null

    // Estados observables por Compose
    var isRouting by mutableStateOf(false)
        private set
    var isPaused by mutableStateOf(false)
        private set

    var nomRuta: String = "Mi ruta"
    var descRuta: String = "Descripción de ejemplo"

    /**
     * 1) Iniciar ruta:
     *    - Cambio inmediato de UI (isRouting = true)
     *    - Creo la ruta en Room y guardo el punto inicial si lo hay
     */
    fun startRoute(name: String, desc: String, initialLocation: GeoPoint?) {
        nomRuta = name
        descRuta = desc

        // UI cambia al instante
        isRouting = true
        isPaused = false
        _routeSegments.clear()
        _routeSegments.add(mutableListOf())

        // Persistir en Room en background
        viewModelScope.launch {
            val newId = dao.insertRoute(
                RouteEntity(name = name, description = desc)
            )
            currentRouteId = newId

            initialLocation?.let {
                dao.insertPosition(
                    PositionEntity(
                        routeId = newId,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                )
                _routeSegments.last().add(it)
            }
        }
    }

    /**
     * 2) Cada vez que llega un GeoPoint:
     *    - Lo persisto en Room
     *    - Lo añado al segmento actual para dibujar
     */
    fun addPoint(point: GeoPoint) {
        val rid = currentRouteId ?: return
        if (isRouting && !isPaused) {
            viewModelScope.launch {
                dao.insertPosition(
                    PositionEntity(
                        routeId = rid,
                        latitude = point.latitude,
                        longitude = point.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            if (_routeSegments.isEmpty()) _routeSegments.add(mutableListOf())
            _routeSegments.last().add(point)
        }
    }

    /**
     * 3) Pausar / reanudar: simplemente actualizamos el estado
     */
    fun togglePause() {
        if (isRouting) {
            isPaused = !isPaused
            if (!isPaused) {
                _routeSegments.add(mutableListOf())
            }
        }
    }

    /**
     * 4) Parar ruta:
     *    - Cambio inmediato de UI (isRouting = false)
     *    - Leo todos los puntos de Room
     *    - Los envío en un solo POST al backend
     *    - Limpio la base de datos local y la UI
     */
    fun stopRoute() {
        isRouting = false
        isPaused = false

        viewModelScope.launch {
            currentRouteId?.let { rid ->
                // 1) Leer posiciones de Room
                val positions = dao.getPositionsForRoute(rid)

                // 2) Construir lista de PosicioDto
                val posicionesDto = positions.mapIndexed { idx, p ->
                    RutaApi.PosicioDto(
                        latitud = p.latitude,
                        longitud = p.longitude,
                        temps = idx                // o timestamp → ajusta si quieres
                    )
                }

                val email = sessionRepository.getCurrentEmail()

                // 3) Construir el DTO principal con el email del usuario log-in
                val dto = RutaApi.RutaDto(
                    id = null,
                    nom = nomRuta,
                    descripcio = descRuta,
                    estat = RutaApi.EstatRutes.INVALIDA,
                    cicloRuta = RutaApi.CicloRuta.FINALITZADA,
                    posicions = posicionesDto,
                    emailUsuari = email
                )

                // 4) Llamar a la API con manejo de errores
                try {
                    val resp = RutaRetrofitInstance.api.saveRuta(dto)
                    if (resp.isSuccessful) {
                        println("Ruta subida: ${resp.body()}")
                    } else {
                        println("HTTP ${resp.code()}: ${resp.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("Excepción de red al subir ruta: $e")
                }

                // 5) Limpiar Room
                dao.deletePositionsForRoute(rid)
                dao.deleteRoute(rid)
                currentRouteId = null
            }
        }

        // 6) Limpiar UI
        _routeSegments.clear()
    }

}
