package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.auth.SessionUser
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.repositories.RecompensaRetrofitInstance
import cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote.RecompensasApiRest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari: StateFlow<Usuari?> = _usuari

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _contra = MutableStateFlow("")
    val contra: StateFlow<String> = _contra

    private val _isUserLoged = MutableStateFlow(false)
    val isUserLoged: StateFlow<Boolean> = _isUserLoged

    private val loginApi: UsuariApi = UsuariRetrofitInstance.retrofitInstance.create(
        UsuariApi::class.java
    )
    fun updateEmail(text: String) {
        _email.value = text
    }

    fun updateContra(text: String) {
        _contra.value = text
    }

    fun resetUserLoged() {
        _isUserLoged.value = false
    }
    suspend fun login(): Usuari? {
        return withContext(Dispatchers.IO) {
            var savedUser: Usuari? = null
            try {
                    val response = loginApi.login(email.value, contra.value)
                    if (response.isSuccessful) {
                        savedUser = response.body()
                        if (savedUser != null) {
                            Log.d("LoginViewModel", "Client loginat amb éxit:  $savedUser")
                            _isUserLoged.value = true
                        } else {
                            Log.d("LoginViewModel", "Client no Activat!:  $savedUser")
                            _isUserLoged.value = false
                            savedUser = null

                        }
                    }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exepció al crear el Client: ${e.message}")
                _isUserLoged.value = false
            }
            savedUser
        }
    }

}
