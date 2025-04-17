package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

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
    private val recompensaApi: ReservaApiRest = ReservaRetrofitInstance.retrofitInstance.create(
        ReservaApiRest::class.java
    )

    private val _reservaD = mutableStateOf<Reserva?>(null)
    val reservaD: State<Reserva?> = _reservaD
    private val _reserva = MutableStateFlow<List<Reserva>>(emptyList())
    val reserva: StateFlow<List<Reserva>> = _reserva


    init {
        LlistarReservas()
    }
    fun crearReserva(reserva: Reserva){
        viewModelScope.launch {
            val response = recompensaApi.save(reserva)
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
                val response = recompensaApi.getById(id)
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
                val response = recompensaApi.findAll()
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


}