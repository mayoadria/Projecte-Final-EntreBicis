package cat.copernic.amayo.frontend.recompensaManagment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import cat.copernic.amayo.frontend.rutaManagment.model.Estat
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class llistaViewmodel : ViewModel() {

    private val recompensaApi: RecompensasApiRest = RecompensaRetrofitInstance.retrofitInstance.create(
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
}