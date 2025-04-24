package cat.copernic.amayo.frontend.core.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitInstance
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(private val sessionRepository: SessionRepository) : ViewModel() {

    private val _userSession = MutableStateFlow(SessionUser("", false))
    val userSession: StateFlow<SessionUser> get() = _userSession

    private val _userData = MutableStateFlow<Usuari?>(null)
    val userData: StateFlow<Usuari?> get() = _userData

    private val userApi: UsuariApi = UsuariRetrofitTLSInstance.retrofitTLSInstance.create(
        UsuariApi::class.java
    )

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            sessionRepository.getSession().collect { session ->
                _userSession.value = session
                Log.i("SessionINFO", "Sesión cargada: ${session.email}, conectado: ${session.isConnected}")

                if (session.isConnected && session.email.isNotEmpty()) {
                    try {
                        val response = userApi.getByEmail(session.email)
                        if (response.isSuccessful) {
                            updateUserData(response.body())
                            Log.i("SessionINFO", "Usuario cargado: ${response.body()?.email}")
                        } else {
                            Log.e("SessionERROR", "Error al cargar usuario: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("SessionERROR", "Excepción: ${e.message}")
                    }
                } else {
                    Log.w("SessionINFO", "No hay sesión activa o email vacío")
                }
            }
        }
    }

    fun updateUserData(client: Usuari?) {
        _userData.value = client
    }

    fun logout() {
        _userSession.value = SessionUser("", false)
        _userData.value = null
        viewModelScope.launch {
            sessionRepository.saveSession(SessionUser("", false))
        }
    }

    fun updateSession(sessionUser: SessionUser) {
        viewModelScope.launch {
            sessionRepository.saveSession(sessionUser)
            _userSession.value = sessionUser
        }
    }
}