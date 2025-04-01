package cat.copernic.amayo.frontend.SistemaManagment.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.amayo.frontend.navigation.BottomNavItem
import cat.copernic.amayo.frontend.navigation.BottomNavigationBar
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil

@Composable
fun BottomNav(navController: NavController){

    val bottomNavController = rememberNavController()
    val viewLlista: llistaViewmodel = viewModel()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inici.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Inici.route) {
                inici()
            }
            composable(BottomNavItem.Rec.route) {
                recompensa(viewLlista,navController
                )
            }
            composable(BottomNavItem.Perfil.route) {
                /* Contenido de esta pantalla */
                perfil()
            }
        }
    }
}
