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