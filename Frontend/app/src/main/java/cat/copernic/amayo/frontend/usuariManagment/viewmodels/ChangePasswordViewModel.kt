package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.core.Logger
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar el procés de canvi de contrasenya d’un usuari.
 * Inclou la validació de dades, enviament de correus, verificació de tokens i actualització de la contrasenya.
 */
class ChangePasswordViewModel : ViewModel() {

    // Preparació de variables
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _codeAuth = MutableStateFlow("")
    val codeAuth: StateFlow<String> = _codeAuth

    private val _realCodeAuth = MutableStateFlow("")
    val realCodeAuth: StateFlow<String> = _realCodeAuth

    private val _contra = MutableStateFlow("")
    val contra: StateFlow<String> = _contra

    private val _newContra = MutableStateFlow("")
    val newContra: StateFlow<String> = _newContra

    private val _repNewContra = MutableStateFlow("")
    val repNewContra: StateFlow<String> = _repNewContra

    //Missatges OK
    private val _emailSuccess = MutableStateFlow<String?>(null)
    val emailSuccess: StateFlow<String?> = _emailSuccess

    private val _codeSuccess = MutableStateFlow<String?>(null)
    val codeSuccess: StateFlow<String?> = _codeSuccess

    //Missatges d'error Empty
    private val _emptyEmailError = MutableStateFlow<String?>(null)
    val emptyEmailError: StateFlow<String?> = _emptyEmailError

    private val _emptyCodeAuthError = MutableStateFlow<String?>(null)
    val emptyCodeAuthError: StateFlow<String?> = _emptyCodeAuthError

    private val _emptyContraError = MutableStateFlow<String?>(null)
    val emptyContraError: StateFlow<String?> = _emptyContraError

    private val _emptyNewContraError = MutableStateFlow<String?>(null)
    val emptyNewContraError: StateFlow<String?> = _emptyNewContraError

    private val _emptyRepNewContraError = MutableStateFlow<String?>(null)
    val emptyRepNewContraError: StateFlow<String?> = _emptyRepNewContraError

    // Missatges d'error Format
    private val _emailNotFoundError = MutableStateFlow<String?>(null)
    val emailNotFoundError: StateFlow<String?> = _emailNotFoundError

    private val _tokenError = MutableStateFlow<String?>(null)
    val tokenError: StateFlow<String?> = _tokenError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _codeError = MutableStateFlow<String?>(null)
    val codeError: StateFlow<String?> = _codeError

    private val _contraError = MutableStateFlow<String?>(null)
    val contraError: StateFlow<String?> = _contraError

    // Comprovació de si s'ha actualitzat la password
    private val _isUserPassUpdated = MutableStateFlow(false)
    val isUserPassUpdated: StateFlow<Boolean> = _isUserPassUpdated

    /**
     * Actualitza el valor de l'email i reinicia els errors relacionats.
     * @param text Nou valor de l'email.
     */
    fun updateEmail(text: String) {
        _email.value = text
        _emptyEmailError.value = null
        _emailError.value = null
        _emailSuccess.value = null
    }

    /**
     * Actualitza el valor del codi d’autenticació i reinicia errors relacionats.
     * @param text Nou codi introduït.
     */
    fun updateCode(text: String) {
        _codeAuth.value = text
        _emptyCodeAuthError.value = null
        _codeError.value = null
    }

    /**
     * Actualitza el valor de la nova contrasenya.
     * @param text Nova contrasenya.
     */
    fun updateNewContra(text: String) {
        _newContra.value = text
        _emptyNewContraError.value = null
    }

    /**
     * Actualitza el valor de la confirmació de nova contrasenya.
     * @param text Repetició de la nova contrasenya.
     */
    fun updateRepNewContra(text: String) {
        _repNewContra.value = text
        _emptyRepNewContraError.value = null
    }

    /**
     * Reinicia l’estat que indica si la contrasenya ha estat modificada correctament.
     */
    fun resetUserPassUpdated() {
        _isUserPassUpdated.value = false
    }

    // Instanciar la Api
    private val usuariApi: UsuariApi = UsuariRetrofitTLSInstance.retrofitTLSInstance.create(
        UsuariApi::class.java
    )

    /**
     * Envia un correu de verificació al correu introduït.
     * Comprova prèviament si l’email és vàlid.
     */
    fun enviarEmail(context : Context) {
        viewModelScope.launch {
            try {
                val isValid = comprovarEmail()
                if (isValid) {
                    val response = usuariApi.sendEmail(email.value)
                    if (response.isSuccessful) {
                        Logger.guardarLog(context, "Correu de verificació enviat correctament a: ${email.value}")
                        Log.d("ChangePasswordViewModel", "Correu enviat amb éxit!")
                        _emailSuccess.value = "Correu enviat amb éxit!"
                    } else if (response.code() == 404) {
                        Logger.guardarLog(context, "Correu no trobat: ${email.value}")
                        Log.e(
                            "LoginViewModel",
                            "No s'ha trobat el correu: ${response.errorBody()?.string()} "
                        )
                        _emailNotFoundError.value = "Correu electònic no registrat!"
                        _realCodeAuth.value = ""
                    } else if (response.code() == 500) {
                        Logger.guardarLog(context, "Error intern al enviar el correu a: ${email.value}")
                        Log.e(
                            "LoginViewModel",
                            "No s'ha enviat el correu: ${response.errorBody()?.string()} "
                        )
                        _emailError.value = "No s'ha pogut enviar el correu!"
                        _realCodeAuth.value = ""
                    }
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció al enviar el correu a ${email.value}: ${e.message}")
                Log.e("CrearCartaViewModel", "Exepció al enviar el Correu: ${e.message}")
            }
        }
    }

    /**
     * Canvia la contrasenya de l’usuari després de validar el codi d’autenticació.
     * Comprova que totes les dades siguin vàlides abans d’enviar la sol·licitud.
     */
    fun changePass(context: Context) {
        viewModelScope.launch {
            try {

                val isValid = comprovarDades()

                if (isValid) {

                    val codeValidate = usuariApi.validateToken(codeAuth.value, email.value)

                    if (codeValidate.isSuccessful) {

                        val user = usuariApi.getByEmail(email.value).body()

                        if (user != null) {

                            user.contra = newContra.value
                            val response = user.email?.let { usuariApi.update(user) }

                            if (response != null) {
                                if (response.isSuccessful) {
                                    val savedUser = response.body()
                                    Logger.guardarLog(context, "Contrasenya actualitzada per a ${email.value}")
                                    Log.d("ChangePasswordViewModel", "Client modificat amb éxit:  $savedUser")
                                    _isUserPassUpdated.value = true
                                } else {
                                    Logger.guardarLog(context, "Error al modificar la contrasenya de ${email.value}")
                                    Log.e(
                                        "ChangePasswordViewModel",
                                        "Error al modificar el Client: ${response.errorBody()?.string()} "
                                    )
                                    _isUserPassUpdated.value = false
                                }
                            }
                        }
                    }else if (codeValidate.code() == 404) {
                        Logger.guardarLog(context, "Codi incorrecte per ${email.value}")
                        Log.e(
                            "ChangePasswordViewModel",
                            "No s'ha trobat el token: ${codeValidate.errorBody()?.string()} "
                        )
                        _codeError.value = "Codi de verificació incorrecte!"
                        _isUserPassUpdated.value = false
                    } else if (codeValidate.code() == 400) {
                        Logger.guardarLog(context, "Correu de verificació incorrecte: ${email.value}")
                        Log.e(
                            "ChangePasswordViewModel",
                            "No s'ha trobat el correu: ${codeValidate.errorBody()?.string()} "
                        )
                        _emailError.value = "Correu de verificació incorrecte!"
                        _isUserPassUpdated.value = false
                    } else if (codeValidate.code() == 401) {
                        Logger.guardarLog(context, "Codi caducat per ${email.value}")
                        Log.e(
                            "ChangePasswordViewModel",
                            "Codi caducat: ${codeValidate.errorBody()?.toString()}"
                        )
                        _codeError.value = "Codi de verificació caducat!"
                        _isUserPassUpdated.value = false
                    }
                    else {
                        Logger.guardarLog(context, "Error desconegut al verificar el token per ${email.value}")
                        Log.e("CrearClientViewModel", "Error al verificar el token!")
                        _codeError.value = "Codi de verificació incorrecte!"
                        _isUserPassUpdated.value = false
                    }
                } else {
                    Logger.guardarLog(context, "Intent fallit de canvi de contrasenya: camps invàlids")
                    Log.e("CrearClientViewModel", "Error al modificar el Client, Camps Buits!")
                    _isUserPassUpdated.value = false
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció al canviar la contrasenya de ${email.value}: ${e.message}")
                Log.e("CrearCartaViewModel", "Exepció al crear el Client: ${e.message}")
                _isUserPassUpdated.value = false
            }
        }
    }

    /**
     * Comprova si el correu introduït és vàlid i retorna si ha passat la validació.
     * @return True si el correu és vàlid, False en cas contrari.
     */
    private fun comprovarEmail(): Boolean {
        var valid = true
        if (email.value.isEmpty()) {
            _emptyEmailError.value = "El camp no pot estar buit!"
            valid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            _emailError.value = "El correu electrònic no és vàlid!"
            valid = false
        }

        return valid
    }

    /**
     * Comprova si totes les dades necessàries per canviar la contrasenya són correctes.
     * Inclou verificació de camps buits, longitud mínima i coincidència de contrasenyes.
     * @return True si totes les dades són vàlides, False en cas contrari.
     */
    private fun comprovarDades(): Boolean {
        var valid = true

        if (codeAuth.value.isEmpty()) {
            _emptyCodeAuthError.value = "El camp no pot estar buit!"
            valid = false
        }

        if (newContra.value.isEmpty()) {
            _emptyNewContraError.value = "El camp no pot estar buit!"
            valid = false
        }

        if (repNewContra.value.isEmpty()) {
            _emptyRepNewContraError.value = "El camp no pot estar buit!"
            valid = false
        }

        if (newContra.value.length < 8) {
            _contraError.value = "La contrasenya ha de tenir almenys 8 caràcters!"
            valid = false
        }

        if (newContra.value != repNewContra.value) {
            _contraError.value = "Les contrasenyes han de coincidir!"
            valid = false
        }

        return valid
    }


}