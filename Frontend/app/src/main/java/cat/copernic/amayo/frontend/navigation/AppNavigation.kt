package cat.copernic.amayo.frontend.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.amayo.frontend.SistemaManagment.ui.BottomNav
import cat.copernic.amayo.frontend.SistemaManagment.ui.inici
import cat.copernic.amayo.frontend.recompensaManagment.ui.detalls
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.ui.LoginScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewLlista: llistaViewmodel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "recompensa",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") { LoginScreen(navController) }
        composable("inici") { BottomNav(navController) }
        composable("recompensa") { recompensa(viewLlista,navController) }
        composable("perfil") { perfil() }
        composable("detalls/{id}") { backStackEntry ->
            val cartId =
                backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
            val viewModel: llistaViewmodel = viewModel()
            detalls(viewModel,cartId) }
    }
}