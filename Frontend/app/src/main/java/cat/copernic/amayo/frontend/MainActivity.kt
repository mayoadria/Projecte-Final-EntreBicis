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
import cat.copernic.amayo.frontend.core.auth.SessionRepository
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.core.auth.ViewModelFactory
import cat.copernic.amayo.frontend.navigation.AppNavigation
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class MainActivity : ComponentActivity() {
    private lateinit var sessionRepository: SessionRepository
    private lateinit var sessionViewModel: SessionViewModel

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