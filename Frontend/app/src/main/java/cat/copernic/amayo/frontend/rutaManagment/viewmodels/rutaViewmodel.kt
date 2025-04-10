package cat.copernic.amayo.frontend.rutaManagment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import cat.copernic.amayo.frontend.rutaManagment.data.repositories.RutaRetrofitInstance
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi

class RutaViewModel : ViewModel() {

    // Lista de segmentos para la visualización en la UI (polyline)
    private val _routeSegments = mutableListOf<MutableList<GeoPoint>>()
    val routeSegments: List<MutableList<GeoPoint>> get() = _routeSegments

    var isRouting: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    var currentRouteId: Long? = null
        private set

    // Iniciar la ruta (crear un identificador y preparar la lista de segmentos)
    fun startRoute(newRouteId: Long) {
        isRouting = true
        isPaused = false
        currentRouteId = newRouteId
        _routeSegments.clear()
        _routeSegments.add(mutableListOf())
    }

    // Agregar un punto al segmento actual para visualizarlo en la UI
    fun addPoint(point: GeoPoint) {
        if (isRouting && !isPaused) {
            if (_routeSegments.isEmpty()) {
                _routeSegments.add(mutableListOf())
            }
            _routeSegments.last().add(point)
        }
    }

    // Llamar al backend para guardar el punto
    fun saveLocationPoint(lat: Double, lon: Double, temps: Int) {
        viewModelScope.launch {
            currentRouteId?.let { routeId ->
                try {
                    val response = RutaRetrofitInstance.api.savePosicio(
                        rutaId = routeId,
                        posicioDto = RutaApi.PosicioDto(latitud = lat, longitud = lon, temps = temps)
                    )
                    if (!response.isSuccessful) {
                        println("Error guardando el punto: ${response.errorBody()?.string()}")
                    } else {
                        println("Punto guardado: ${response.body()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Iniciar un loop que envíe la posición actual cada segundo
    fun startSavingPoints(getLocation: () -> GeoPoint?) {
        viewModelScope.launch {
            while (isRouting && !isPaused) {
                val location = getLocation()
                if (location != null) {
                    addPoint(location)
                    // El valor de 'temps' puede ser el tiempo transcurrido, en este ejemplo se usa 1
                    saveLocationPoint(location.latitude, location.longitude, 1)
                }
                delay(1000L)
            }
        }
    }

    // Alternar entre pausar y reanudar la grabación
    fun togglePause() {
        if (isRouting) {
            isPaused = !isPaused
            if (!isPaused) {
                _routeSegments.add(mutableListOf())
            }
        }
    }

    // Finalizar la ruta
    fun stopRoute() {
        isRouting = false
        isPaused = false
        currentRouteId = null
    }
}
