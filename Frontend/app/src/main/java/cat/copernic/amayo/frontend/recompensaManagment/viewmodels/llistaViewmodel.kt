package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import android.content.ContentResolver
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.RecompensaRetrofitTLSInstance
import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.rutaManagment.model.Estat
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la información relacionada con las recompensas.
 * Este ViewModel es responsable de cargar y gestionar los datos de las recompensas
 * desde una API remota, así como de almacenar y actualizar los detalles de las recompensas.
 *
 * @property recompensaApi API que maneja las operaciones de recompensas a través de Retrofit.
 * @property descripcio Descripción de la recompensa.
 * @property cost Costo de la recompensa en puntos.
 * @property estat Estado de la recompensa.
 * @property observacions Observaciones adicionales de la recompensa.
 * @property imatge Imagen de la recompensa en formato base64.
 * @property recompesa Lista de todas las recompensas disponibles.
 * @property recompensaD Recompensa seleccionada, generalmente para detalles específicos.
 *
 * **Funciones principales**:
 * - `LlistarRecompenses`: Carga todas las recompensas disponibles desde la API.
 * - `listar`: Carga los detalles de una recompensa específica por su ID.
 * - `updateRecompensa`: Actualiza la información de una recompensa en la API.
 */
class llistaViewmodel : ViewModel() {

    private val recompensaApi: RecompensasApiRest = RecompensaRetrofitTLSInstance.retrofitTLSInstance.create(
        RecompensasApiRest::class.java
    )

    private val _descripcio = MutableStateFlow("")
    val descripcio: StateFlow<String> = _descripcio
    private val _cost = MutableStateFlow(0)
    val cost: StateFlow<Int> = _cost
    private val _estat = MutableStateFlow<Estat>(Estat.INACTIU)
    val estat: StateFlow<Estat> = _estat
    private val _observacions = MutableStateFlow("")
    val observacions: StateFlow<String> = _observacions
    private val _imatge = MutableStateFlow<String?>(null)
    val imatge: StateFlow<String?> = _imatge

    private val _recompesa = MutableStateFlow<List<Recompensa>>(emptyList())
    val recompesa: StateFlow<List<Recompensa>> = _recompesa


    private val _recompensaD = mutableStateOf<Recompensa?>(null)
    val recompensaD: State<Recompensa?> = _recompensaD
    init {
        LlistarRecompenses()
    }
    /**
     * Carga todas las recompensas disponibles desde la API.
     * Los resultados se almacenan en el estado `recompesa`.
     */
    fun LlistarRecompenses() {
        viewModelScope.launch {
            try {
                val response = recompensaApi.findAll()
                if (response.isSuccessful) {
                    _recompesa.value = response.body() ?: emptyList()
                } else {
                    println("Error!!")
                }

            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    /**
     * Carga los detalles de una recompensa específica a partir de su ID.
     * Los detalles se almacenan en el estado `recompensaD`.
     *
     * @param id El ID de la recompensa que se desea cargar.
     */
    fun listar(id: Long) {
        viewModelScope.launch {
            try {
                val response = recompensaApi.getById(id)
                if (response.isSuccessful) {
                    _recompensaD.value = response.body()
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    /**
     * Actualiza la información de una recompensa en la API.
     *
     * @param client La recompensa con los nuevos datos que se deben guardar.
     * @param contentResolver El `ContentResolver` utilizado para acceder a los datos del cliente.
     */
    fun updateRecompensa(client: Recompensa, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {

                // Llamada al nuevo endpoint sin clientId separado
                val response = recompensaApi.update(client)
                if (response.isSuccessful) {
                    Log.d("ModificarViewModel", "Usuario actualizado con éxito")
                } else {
                    Log.e(
                        "ModificarViewModel",
                        "Error al actualizar el usuario: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ModificarViewModel", "Excepción al actualizar el usuario: ${e.message}")
            }
        }
    }
}