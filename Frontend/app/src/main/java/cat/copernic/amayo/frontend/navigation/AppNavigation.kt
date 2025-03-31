package cat.copernic.amayo.frontend.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.amayo.frontend.SistemaManagment.ui.BottomNav
import cat.copernic.amayo.frontend.SistemaManagment.ui.inici
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.usuariManagment.ui.LoginScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") { LoginScreen(navController) }
        composable("inici") { BottomNav(navController) }
        composable("recompensa") { recompensa() }
        composable("perfil") { perfil() }
    }
}