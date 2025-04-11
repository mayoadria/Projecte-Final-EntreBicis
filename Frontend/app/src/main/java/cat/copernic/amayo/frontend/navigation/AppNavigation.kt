package cat.copernic.amayo.frontend.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import cat.copernic.amayo.frontend.SistemaManagment.ui.BottomNav
//import cat.copernic.amayo.frontend.sistemaManagment.ui.inici
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.core.auth.SplashScreen
import cat.copernic.amayo.frontend.sistemaManagment.ui.BottomNav
import cat.copernic.amayo.frontend.recompensaManagment.ui.detalls
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.rutaManagment.ui.RutaScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.EditarPerfil
import cat.copernic.amayo.frontend.usuariManagment.ui.LoginScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.LoginViewModel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel

@Composable
fun AppNavigation(sessionViewModel: SessionViewModel) {
    val navController = rememberNavController()
    val viewLlista: llistaViewmodel = viewModel()
    val loginView: LoginViewModel = viewModel()
    val modificarView: ModificarViewModel = viewModel()
    val reservaView: ReservaViewmodel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("splash") { SplashScreen(navController, sessionViewModel) }
        composable("login") { LoginScreen(navController, loginView,sessionViewModel) }
        composable("inici") { BottomNav(navController,sessionViewModel) }
        composable("rutas") { RutaScreen(navController) }
        composable("recompensa") { recompensa(viewLlista,navController,sessionViewModel,false) }
        composable("recompensaPropias") { recompensa(viewLlista,navController,sessionViewModel,true) }
        composable("perfil") { perfil(sessionViewModel,navController) }
        composable("detalls/{id}") { backStackEntry ->
            val cartId =
                backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
            val viewModel: llistaViewmodel = viewModel()
            detalls(viewModel,cartId,reservaView,sessionViewModel,modificarView,navController) }
        composable("editar") {
            EditarPerfil(sessionViewModel,modificarView,navController)
        }
    }
}