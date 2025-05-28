package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import cat.copernic.amayo.frontend.core.Logger
import cat.copernic.amayo.frontend.rutaManagment.model.Estat
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * ViewModel encarregat de gestionar la lògica d'inici de sessió d'un usuari.
 * Inclou validacions, trucades a l'API, gestió d'estat i missatges d'error.
 */
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

    /**
     * Actualitza el valor del correu electrònic i neteja errors associats.
     * @param text Nou valor per al camp email.
     */
    fun updateEmail(text: String) {
        _email.value = text.trim()

        if (text.isNotBlank()) {
            _emptyEmailError.value = null
            _authError.value = null          // oculta error general al editar
        }
    }

    /**
     * Actualitza el valor de la contrasenya i neteja errors associats.
     * @param text Nou valor per al camp contrasenya.
     */
    fun updateContra(text: String) {
        _contra.value = text

        if (text.isNotBlank()) {
            _emptyContraError.value = null
            _authError.value = null
        }
    }

    /**
     * Reinicia l'estat de connexió de l'usuari després d'un login correcte.
     */
    fun resetUserLoged() {
        _isUserLoged.value = false
    }

    /**
     * Realitza el procés d'inici de sessió comprovant les dades i validant la resposta de l'API.
     * Controla l'estat de l'usuari i gestiona possibles errors.
     * @return Usuari autenticat o null si ha fallat.
     */
    suspend fun login(context: Context): Usuari? = withContext(Dispatchers.IO) {
        _authError.value = null
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
                            Logger.guardarLog(context, "Login correcte per a: ${email.value}")
                        }

                        Estat.INACTIU -> {
                            _isUserLoged.value = false
                            _authError.value = "Compte no activat! Comprova el teu correu."
                            Logger.guardarLog(context, "Login fallit (usuari inactiu): ${email.value}")
                        }

                        else -> {
                            _isUserLoged.value = false
                            _authError.value = "No s'ha pogut iniciar sessió."
                            Logger.guardarLog(context, "Login fallit (estat desconegut): ${email.value}")
                        }
                    }
                } else {
                    _isUserLoged.value = false
                    _authError.value = when (response.code()) {
                        401 -> "Correu o contrasenya incorrectes!"
                        404 -> "El correu no existeix!"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    Logger.guardarLog(context, "Login fallit [${response.code()}]: ${email.value}")
                }
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Excepció al login: ${e.localizedMessage}")
            _isUserLoged.value = false
            _authError.value = "Error inesperat: ${e.localizedMessage}"
            Logger.guardarLog(context, "Login fallit per excepció: ${e.localizedMessage}")
        }

        loggedUser
    }


    /**
     * Comprova si els camps email i contrasenya compleixen les validacions bàsiques.
     * @return True si les dades són vàlides, False si hi ha errors.
     */
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
