package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari: StateFlow<Usuari?> = _usuari

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    private val loginApi: UsuariApi = UsuariRetrofitInstance.retrofitInstance.create(
        UsuariApi::class.java
    )
    fun login(correu: String, contrasenya: String) {
        viewModelScope.launch {
            try {
                val response = loginApi.login(correu, contrasenya)

                if (response.isSuccessful) {
                    _usuari.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Credencials incorrectes: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de connexió: ${e.message}"
            }
        }
    }
}
