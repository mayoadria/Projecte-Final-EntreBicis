package cat.copernic.amayo.frontend.Session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repositori responsable de gestionar la sessió de l'usuari mitjançant DataStore.
 * Emmagatzema i recupera les dades de sessió (email i estat de connexió) de manera persistent.
 *
 * @param dataStore Instància de [DataStore] utilitzada per accedir a les preferències.
 */
class SessionRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        /** Clau per emmagatzemar el correu electrònic de l'usuari. */
        private val USER_UNAME_KEY = stringPreferencesKey("user_userName")
        /** Clau per emmagatzemar si l'usuari està connectat o no. */
        private val IS_CONNECTED_KEY = booleanPreferencesKey("is_connected")
    }

    /**
     * Desa la informació de sessió de l'usuari al DataStore.
     *
     * @param sessionUser Objecte [SessionUser] amb les dades a desar (email i connexió).
     */
    suspend fun saveSession(sessionUser: SessionUser) {
        dataStore.edit { preferences ->
            preferences[USER_UNAME_KEY] = sessionUser.email
            preferences[IS_CONNECTED_KEY] = sessionUser.isConnected

        }
    }

    /**
     * Retorna un [Flow] amb l'estat actual de la sessió de l'usuari.
     *
     * @return [Flow] de [SessionUser] amb les dades emmagatzemades.
     */
    fun getSession(): Flow<SessionUser> {
        return dataStore.data.map { preferences ->
            val email = preferences[USER_UNAME_KEY] ?: ""
            val isConnected = preferences[IS_CONNECTED_KEY] ?: false
            SessionUser(email, isConnected)
        }
    }

    /**
     * Retorna l'email de l'usuari actualment guardat a la sessió.
     * Aquesta funció és de tipus `suspend` per a ser utilitzada dins de corutines.
     *
     * @return El correu electrònic de la sessió o una cadena buida si no n'hi ha.
     */
    suspend fun getCurrentEmail(): String =
        dataStore.data.first()[USER_UNAME_KEY] ?: ""
}