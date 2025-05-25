package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.Logger
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

    private val recompensaApi: RecompensasApiRest =
        RecompensaRetrofitTLSInstance.retrofitTLSInstance.create(
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

    /**
     * Carga todas las recompensas disponibles desde la API.
     * Los resultados se almacenan en el estado `recompesa`.
     */
    fun LlistarRecompenses(context: Context) {
        viewModelScope.launch {
            try {
                Logger.guardarLog(context, "Iniciant càrrega de recompenses")
                val response = recompensaApi.findAll()
                if (response.isSuccessful) {
                    _recompesa.value = response.body() ?: emptyList()
                    Logger.guardarLog(
                        context,
                        "Recompenses carregades correctament: ${_recompesa.value.size} elements"
                    )
                } else {
                    Logger.guardarLog(context, "Error carregant recompenses: ${response.code()}")
                }

            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció a LlistarRecompenses: ${e.message}")
            }
        }
    }

    /**
     * Carga los detalles de una recompensa específica a partir de su ID.
     * Los detalles se almacenan en el estado `recompensaD`.
     *
     * @param id El ID de la recompensa que se desea cargar.
     */
    fun listar(id: Long, context: Context) {
        viewModelScope.launch {
            try {
                Logger.guardarLog(context, "Carregant detall de recompensa amb ID: $id")
                val response = recompensaApi.getById(id)
                if (response.isSuccessful) {
                    _recompensaD.value = response.body()
                    Logger.guardarLog(
                        context,
                        "Recompensa carregada: ${_recompensaD.value?.descripcio}"
                    )
                } else {
                    Logger.guardarLog(context, "Error carregant recompensa $id: ${response.code()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció a listar($id): ${e.message}")
            }
        }
    }
        /**
         * Actualiza la información de una recompensa en la API.
         *
         * @param client La recompensa con los nuevos datos que se deben guardar.
         * @param contentResolver El `ContentResolver` utilizado para acceder a los datos del cliente.
         */
        fun updateRecompensa(client: Recompensa, contentResolver: ContentResolver, context: Context) {
            viewModelScope.launch {
                try {
                    Logger.guardarLog(context, "Actualitzant recompensa amb ID: ${client.id}")
                    val response = recompensaApi.update(client)
                    if (response.isSuccessful) {
                        Logger.guardarLog(context, "Recompensa actualitzada correctament")
                    } else {
                        Logger.guardarLog(context, "Error actualitzant recompensa: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Logger.guardarLog(context, "Excepció a updateRecompensa: ${e.message}")
                }
            }
        }

}
