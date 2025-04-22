package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import android.content.ContentResolver
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.ReservaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.ReservaApiRest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservaViewmodel : ViewModel(){
    private val reservaApi: ReservaApiRest = ReservaRetrofitInstance.retrofitInstance.create(
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