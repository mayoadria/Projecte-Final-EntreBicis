package cat.copernic.amayo.frontend.Session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.delay

/**
 * Composable que representa la pantalla inicial (Splash Screen) de l'aplicació.
 *
 * Mostra una animació de càrrega mentre es comprova l'estat de la sessió de l'usuari.
 * Un cop determinat si hi ha una sessió activa, redirigeix automàticament a la pantalla d'inici
 * o a la de login.
 *
 * @param navController Controlador de navegació per redirigir entre pantalles.
 * @param sessionViewModel ViewModel que conté l'estat de la sessió de l'usuari.
 */
@Composable
fun SplashScreen(navController: NavController, sessionViewModel: SessionViewModel) {
    val userSession by sessionViewModel.userSession.collectAsState()

    LaunchedEffect(userSession) {
        delay(750)
        if (userSession.isConnected) {
            navController.navigate("inici") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Diseño de la pantalla de carga (puedes personalizarlo)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}