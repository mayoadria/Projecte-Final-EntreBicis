package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import cat.copernic.amayo.frontend.rutaManagment.model.Estat
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _emptyEmailError = MutableStateFlow<String?>(null)
    val emptyEmailError: StateFlow<String?> = _emptyEmailError

    private val _emptyContraError = MutableStateFlow<String?>(null)
    val emptyContraError: StateFlow<String?> = _emptyContraError

    private val _emailNotFoundError = MutableStateFlow<String?>(null)
    val emailNotFoundError: StateFlow<String?> = _emailNotFoundError
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError



    private val loginApi: UsuariApi = UsuariRetrofitTLSInstance.retrofitTLSInstance.create(
        UsuariApi::class.java
    )

    fun updateEmail(text: String) {
        _email.value = text.trim()

        if (text.isNotBlank()) {
            _emptyEmailError.value = null
            _authError.value = null          // oculta error general al editar
        }
    }

    fun updateContra(text: String) {
        _contra.value = text

        if (text.isNotBlank()) {
            _emptyContraError.value = null
            _authError.value = null
        }
    }

    fun resetUserLoged() {
        _isUserLoged.value = false
    }

    suspend fun login(): Usuari? = withContext(Dispatchers.IO) {
        _authError.value = null               // limpia errores de intentos previos
        var loggedUser: Usuari? = null

        try {
            if (comprovarDades()) {
                val response = loginApi.login(email.value, contra.value)

                if (response.isSuccessful) {
                    val user = response.body()

                    when (user?.estat) {
                        Estat.ACTIU -> {
                            _usuari.value = user
                            _isUserLoged.value = true
                            loggedUser = user
                        }

                        Estat.INACTIU -> {
                            _isUserLoged.value = false
                            _authError.value = "Compte no activat! Comprova el teu correu."
                        }

                        else -> {
                            _isUserLoged.value = false
                            _authError.value = "No s'ha pogut iniciar sessió."
                        }
                    }
                } else {                       // códigos ≠ 2xx
                    _isUserLoged.value = false
                    _authError.value = when (response.code()) {
                        401 -> "Correu o contrasenya incorrectes!"
                        404 -> "El correu no existeix!"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Excepció al login: ${e.localizedMessage}")
            _isUserLoged.value = false
            _authError.value = "Error inesperat: ${e.localizedMessage}"
        }

        loggedUser
    }

    private fun comprovarDades(): Boolean {
        var valid = true

        if (email.value.isEmpty()) {
            _emptyEmailError.value = "El camp no pot estar buit!"
            valid = false
        }

        if(contra.value.isEmpty()){
            _emptyContraError.value = "El camp no pot estar buit!"
            valid = false
        }

        return valid
    }

}
