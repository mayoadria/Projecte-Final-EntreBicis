package cat.copernic.amayo.frontend.Session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar l'estat de la sessió de l'usuari.
 *
 * <p>Gestiona la persistència i recuperació de les dades de sessió (email, connexió) mitjançant el [SessionRepository].
 * A més, obté les dades completes de l'usuari des de l'API i exposa aquesta informació a la UI.</p>
 *
 * @property sessionRepository Repositori per a la gestió de dades de sessió amb DataStore.
 */
class SessionViewModel(private val sessionRepository: SessionRepository) : ViewModel() {

    /** Estat de la sessió de l'usuari actual com a [StateFlow]. */
    private val _userSession = MutableStateFlow(SessionUser("", false))
    val userSession: StateFlow<SessionUser> get() = _userSession

    /** Dades completes de l'usuari carregades des de l'API. */
    private val _userData = MutableStateFlow<Usuari?>(null)
    val userData: StateFlow<Usuari?> get() = _userData

    fun updateSaldo(nuevoSaldo: Double) {
        val usuarioActual = _userData.value
        if (usuarioActual != null) {
            val usuarioActualizado = usuarioActual.copy(saldo = nuevoSaldo)
            _userData.value = usuarioActualizado
        }
    }
    fun actualizarReserva(valor: Boolean) {
        val usuarioActual = _userData.value
        if (usuarioActual != null) {
            val usuarioActualizado = usuarioActual.copy(reserva = valor)
            _userData.value = usuarioActualizado
        }
    }


    /** Instància de l'API per recuperar informació de l'usuari. */
    private val userApi: UsuariApi = UsuariRetrofitTLSInstance.retrofitTLSInstance.create(
        UsuariApi::class.java
    )

    /**
     * Bloc d'inicialització per carregar l'estat de la sessió a l'arrencada.
     */
    init {
        loadSession()
    }

    /**
     * Carrega la sessió guardada des de DataStore i actualitza l'estat intern.
     * Si la sessió és vàlida, obté les dades completes de l'usuari des de l'API.
     */
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


    /**
     * Actualitza les dades de l'usuari al [StateFlow] local.
     *
     * @param client Objecte [Usuari] amb les dades a actualitzar.
     */
    fun updateUserData(client: Usuari?) {
        _userData.value = client
    }

    /**
     * Finalitza la sessió de l'usuari, buidant la informació guardada i reiniciant l'estat.
     */
    fun logout() {
        _userSession.value = SessionUser("", false)
        _userData.value = null
        viewModelScope.launch {
            sessionRepository.saveSession(SessionUser("", false))
        }
    }

    /**
     * Actualitza l'estat de la sessió (email i connexió) i el desa persistentment.
     *
     * @param sessionUser Objecte [SessionUser] amb les noves dades de sessió.
     */
    fun updateSession(sessionUser: SessionUser) {
        viewModelScope.launch {
            sessionRepository.saveSession(sessionUser)
            _userSession.value = sessionUser
        }
    }
}