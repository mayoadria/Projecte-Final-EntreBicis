package cat.copernic.amayo.frontend.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val USER_UNAME_KEY = stringPreferencesKey("user_userName")
        private val IS_CONNECTED_KEY = booleanPreferencesKey("is_connected")
    }

    suspend fun saveSession(sessionUser: SessionUser) {
        dataStore.edit { preferences ->
            preferences[USER_UNAME_KEY] = sessionUser.email
            preferences[IS_CONNECTED_KEY] = sessionUser.isConnected

        }
    }

    fun getSession(): Flow<SessionUser> {
        return dataStore.data.map { preferences ->
            val email = preferences[USER_UNAME_KEY] ?: ""
            val isConnected = preferences[IS_CONNECTED_KEY] ?: false
            SessionUser(email, isConnected)
        }
    }

    /**
     * Devuelve el e-mail almacenado actualmente (o "" si no hay sesión).
     * Es `suspend` para poder llamarse dentro de corutinas.
     */
    suspend fun getCurrentEmail(): String =
        dataStore.data.first()[USER_UNAME_KEY] ?: ""
}