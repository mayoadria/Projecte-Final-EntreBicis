package cat.copernic.amayo.frontend.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.amayo.frontend.Session.SessionViewModel
import cat.copernic.amayo.frontend.Session.SplashScreen
import cat.copernic.amayo.frontend.recompensaManagment.ui.DetallsReserva
import cat.copernic.amayo.frontend.recompensaManagment.ui.RecompensaEntregadaScreen
import cat.copernic.amayo.frontend.sistemaManagment.ui.BottomNav
import cat.copernic.amayo.frontend.recompensaManagment.ui.detalls
import cat.copernic.amayo.frontend.recompensaManagment.ui.recompensa
import cat.copernic.amayo.frontend.recompensaManagment.ui.reservesPropies
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.rutaManagment.ui.RutaScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.ChangePasswordScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.EditarPerfil
import cat.copernic.amayo.frontend.usuariManagment.ui.LoginScreen
import cat.copernic.amayo.frontend.usuariManagment.ui.perfil
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ChangePasswordViewModel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.LoginViewModel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel

/**
 * Configuració principal de la navegació de l'aplicació mitjançant Jetpack Compose Navigation.
 *
 * Defineix totes les rutes i pantalles disponibles dins de l'aplicació, incloent:
 *     Splash screen
 *     Login
 *     Pantalla inicial amb Bottom Navigation
 *     Gestió de recompenses, reserves i perfil d'usuari
 *     Edició de perfil i canvi de contrasenya
 *
 * Utilitza un [NavHost] per gestionar la navegació i comparteix el [SessionViewModel] entre pantalles.
 *
 * @param sessionViewModel ViewModel que gestiona l'estat de la sessió d'usuari.
 */
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
        composable("login") { LoginScreen(navController, loginView, sessionViewModel) }

        composable("inici") {
            BottomNav(navController, sessionViewModel)
        }

        composable("rutas") {
            ScaffoldWrapper(navController) {
                RutaScreen(navController)
            }
        }

        composable("recompensa") {
            ScaffoldWrapper(navController) {
                recompensa(viewLlista, navController, sessionViewModel)
            }
        }

        composable("recompensaPropias") {
            ScaffoldWrapper(navController) {
                reservesPropies(reservaView, navController, sessionViewModel)
            }
        }

        composable("perfil") {
            ScaffoldWrapper(navController) {
                perfil(sessionViewModel, navController)
            }
        }

        composable("detalls/{id}") { backStackEntry ->
            val idParam = backStackEntry.arguments?.getString("id")
            val cartId = idParam?.toLongOrNull() ?: return@composable

            ScaffoldWrapper(navController) {
                detalls(
                    viewLlista,
                    cartId,
                    reservaView,
                    sessionViewModel,
                    modificarView,
                    navController
                )
            }
        }

        composable("detallsReserva/{id}") { backStackEntry ->
            val idParam = backStackEntry.arguments?.getString("id")
            val cartId = idParam?.toLongOrNull() ?: return@composable

            ScaffoldWrapper(navController) {
                DetallsReserva(
                    cartId,
                    reservaView,
                    viewLlista,
                    sessionViewModel,
                    modificarView,
                    navController
                )
            }
        }

        composable("editar") {
            ScaffoldWrapper(navController) {
                EditarPerfil(sessionViewModel, modificarView, navController)
            }
        }

        composable("check") {
            ScaffoldWrapper(navController) {
                RecompensaEntregadaScreen(navController)
            }
        }

        composable("changePass") {
            ScaffoldWrapper(navController) {
                ChangePasswordScreen(ChangePasswordViewModel(), navController)
            }
        }
    }
}
