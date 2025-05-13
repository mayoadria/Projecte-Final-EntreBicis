package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import android.content.ContentResolver
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        LlistarReservas()
    }

    /**
     * Crea una nueva reserva en la API.
     *
     * @param reserva La reserva que se desea crear.
     */
    fun crearReserva(reserva: Reserva){
        viewModelScope.launch {
            val response = reservaApi.save(reserva)
            if (response.isSuccessful) {
                val savedCarta = response.body()
                Log.d("CrearReservaViewModel", "Reserva guardada con éxito: $savedCarta")
            } else {
                Log.e("CrearReservaViewModel", "Error al guardar la reserva: ${response.errorBody()?.string()}")
            }
        }
    }

    /**
     * Carga los detalles de una reserva específica a partir de su ID.
     *
     * @param id El ID de la reserva que se desea cargar.
     */
    fun listar(id: Long) {
        viewModelScope.launch {
            try {
                val response = reservaApi.getById(id)
                if (response.isSuccessful) {
                    _reservaD.value = response.body()
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
    /**
     * Carga todas las reservas disponibles desde la API.
     * Los resultados se almacenan en el estado `reserva`.
     */
    fun LlistarReservas() {
        viewModelScope.launch {
            try {
                val response = reservaApi.findAll()
                if (response.isSuccessful) {
                    _reserva.value = response.body() ?: emptyList()
                } else {
                    println("Error!!")
                }

            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
    /**
     * Actualiza la información de una reserva en la API.
     *
     * @param client La reserva con los nuevos datos que se deben guardar.
     */
    fun updateReserva(client: Reserva) {
        viewModelScope.launch {
            try {

                // Llamada al nuevo endpoint sin clientId separado
                val response = reservaApi.update(client)
                if (response.isSuccessful) {
                    Log.d("ModificarViewModel", "Reserva actualizada con éxito")
                } else {
                    Log.e(
                        "ModificarViewModel",
                        "Error al actualizar la Reserva: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ModificarViewModel", "Excepción al actualizar la Reserva: ${e.message}")
            }
        }
    }
    /**
     * Elimina una reserva específica de la API.
     *
     * @param cartaId El ID de la reserva que se desea eliminar.
     */
    fun borrar(cartaId: Long) {
        viewModelScope.launch {
            try {
                val response = reservaApi.deleteById(cartaId)
                if (response.isSuccessful) {
                    _deleteSuccess.value = true
                    LlistarReservas()// Marcamos como exitosa la eliminación
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }


}