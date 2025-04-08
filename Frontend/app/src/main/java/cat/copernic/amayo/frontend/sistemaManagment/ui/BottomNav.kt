package cat.copernic.amayo.frontend.sistemaManagment.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.navigation.BottomNavItem
import cat.copernic.amayo.frontend.navigation.BottomNavigationBar
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil
import cat.copernic.amayo.frontend.sistemaManagment.ui.Inici  // Import necesario

@Composable
fun BottomNav(navController: NavController,sessionViewModel: SessionViewModel){

    val bottomNavController = rememberNavController()
    val viewLlista: llistaViewmodel = viewModel()
    val nom by sessionViewModel.userData.collectAsState()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inici.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantalla de inici: el botón "+" redirige a "rutas" usando el navController de AppNavigation
            composable(BottomNavItem.Inici.route) {
                Inici(navController = navController)
            }
            composable(BottomNavItem.Rec.route) {
                recompensa(viewLlista, navController)
            }
            composable(BottomNavItem.Perfil.route) {
                perfil()
            }
        }
    }
}
