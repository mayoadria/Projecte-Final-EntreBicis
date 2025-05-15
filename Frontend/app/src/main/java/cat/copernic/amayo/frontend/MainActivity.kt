package cat.copernic.amayo.frontend

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import cat.copernic.amayo.frontend.Session.SessionRepository
import cat.copernic.amayo.frontend.Session.SessionViewModel
import cat.copernic.amayo.frontend.Session.ViewModelFactory
import cat.copernic.amayo.frontend.navigation.AppNavigation
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

/**
 * Extensió de la classe Context per proporcionar un DataStore de preferències amb el nom "session".
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
/**
 * Activitat principal de l'aplicació.
 * S'encarrega d'inicialitzar el SessionRepository i el SessionViewModel per gestionar la sessió de l'usuari.
 * També configura la navegació principal i la configuració d'orientació de la pantalla.
 */
class MainActivity : ComponentActivity() {
    private lateinit var sessionRepository: SessionRepository
    private lateinit var sessionViewModel: SessionViewModel

    /**
     * S'executa en crear l'activitat.
     * - Bloqueja la rotació a vertical.
     * - Inicialitza el SessionRepository i SessionViewModel.
     * - Llama a la navegació principal de l'aplicació.
     * - Configura el UserAgent per la llibreria de mapes (OpenStreetMap).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            sessionRepository =
                SessionRepository(dataStore = applicationContext.dataStore)
            sessionViewModel = ViewModelProvider(this, ViewModelFactory(sessionRepository))
                .get(SessionViewModel::class.java)
            AppNavigation(sessionViewModel)
        }

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}