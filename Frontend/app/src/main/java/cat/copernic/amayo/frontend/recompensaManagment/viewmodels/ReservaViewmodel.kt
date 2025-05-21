package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.Logger
import cat.copernic.amayo.frontend.recompensaManagment.data.repositories.ReservaRetrofitTLSInstance
import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.ReservaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.ReservaApiRest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la información relacionada con las reservas de recompensas.
 * Este ViewModel es responsable de interactuar con la API remota para crear, listar, actualizar y eliminar reservas.
 *
 * @property reservaApi API que maneja las operaciones de reservas a través de Retrofit.
 * @property reservaD Reserva seleccionada, generalmente para mostrar detalles.
 * @property reserva Lista de todas las reservas disponibles.
 * @property deleteSuccess Estado que indica si la eliminación de una reserva fue exitosa.
 *
 * **Funciones principales**:
 * - `crearReserva`: Crea una nueva reserva enviando los datos a la API.
 * - `listar`: Carga los detalles de una reserva específica a partir de su ID.
 * - `LlistarReservas`: Carga todas las reservas disponibles desde la API.
 * - `updateReserva`: Actualiza los detalles de una reserva en la API.
 * - `borrar`: Elimina una reserva específica de la API.
 */
class ReservaViewmodel : ViewModel(){
    private val reservaApi: ReservaApiRest = ReservaRetrofitTLSInstance.retrofitTLSInstance.create(
        ReservaApiRest::class.java
    )

    private val _reservaD = mutableStateOf<Reserva?>(null)
    val reservaD: State<Reserva?> = _reservaD
    private val _reserva = MutableStateFlow<List<Reserva>>(emptyList())
    val reserva: StateFlow<List<Reserva>> = _reserva
    private val _deleteSuccess = mutableStateOf(false) // Estado para controlar la eliminación
    val deleteSuccess: State<Boolean> = _deleteSuccess


    /**
     * Crea una nueva reserva en la API.
     *
     * @param reserva La reserva que se desea crear.
     */
    fun crearReserva(reserva: Reserva, context: Context) {
        viewModelScope.launch {
            val response = reservaApi.save(reserva)
            if (response.isSuccessful) {
                val savedCarta = response.body()
                Logger.guardarLog(context, "Reserva creada amb èxit: $savedCarta")
            } else {
                Logger.guardarLog(context, "Error creant la reserva: ${response.errorBody()?.string()}")
            }
        }
    }


    /**
     * Carga los detalles de una reserva específica a partir de su ID.
     *
     * @param id El ID de la reserva que se desea cargar.
     */
    fun listar(id: Long, context: Context) {
        viewModelScope.launch {
            try {
                val response = reservaApi.getById(id)
                if (response.isSuccessful) {
                    _reservaD.value = response.body()
                    Logger.guardarLog(context, "Reserva carregada: ${_reservaD.value}")
                } else {
                    Logger.guardarLog(context, "Error carregant reserva $id: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció carregant reserva $id: ${e.message}")
            }
        }
    }
    /**
     * Carga todas las reservas disponibles desde la API.
     * Los resultados se almacenan en el estado `reserva`.
     */
    fun LlistarReservas(context: Context) {
        viewModelScope.launch {
            try {
                val response = reservaApi.findAll()
                if (response.isSuccessful) {
                    _reserva.value = response.body() ?: emptyList()
                    Logger.guardarLog(context, "Reserves carregades: ${_reserva.value.size} elements")
                } else {
                    Logger.guardarLog(context, "Error carregant reserves: ${response.code()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció carregant reserves: ${e.message}")
            }
        }
    }
    /**
     * Actualiza la información de una reserva en la API.
     *
     * @param client La reserva con los nuevos datos que se deben guardar.
     */
    fun updateReserva(client: Reserva, context: Context) {
        viewModelScope.launch {
            try {
                val response = reservaApi.update(client)
                if (response.isSuccessful) {
                    Logger.guardarLog(context, "Reserva actualitzada amb èxit: ${client.id}")
                } else {
                    Logger.guardarLog(context, "Error actualitzant reserva: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció actualitzant reserva: ${e.message}")
            }
        }
    }
    /**
     * Elimina una reserva específica de la API.
     *
     * @param cartaId El ID de la reserva que se desea eliminar.
     */
    fun borrar(cartaId: Long, context: Context) {
        viewModelScope.launch {
            try {
                val response = reservaApi.deleteById(cartaId)
                if (response.isSuccessful) {
                    _deleteSuccess.value = true
                    Logger.guardarLog(context, "Reserva eliminada: ID $cartaId")
                    LlistarReservas(context) // Tornem a carregar
                } else {
                    Logger.guardarLog(context, "Error eliminant reserva $cartaId: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció eliminant reserva $cartaId: ${e.message}")
            }
        }
    }


}