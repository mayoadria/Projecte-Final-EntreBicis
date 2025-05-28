package cat.copernic.amayo.frontend.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Composable que defineix la barra de navegació inferior de l'aplicació.
 *
 * Mostra una barra inferior amb diverses opcions de navegació (Inici, Recompenses, Perfil)
 * i gestiona la navegació entre aquestes rutes mitjançant el [NavController].
 * La icona i etiqueta de cada opció es defineixen a la classe [BottomNavItem].
 *
 * Comportament:
 *     Marca com seleccionat l'element corresponent a la ruta actual.
 *     En clicar una opció, navega a la ruta corresponent amb gestió d'estat i stack.
 * @param navController Controlador de navegació per gestionar el canvi de pantalles.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Inici,
        BottomNavItem.Rec,
        BottomNavItem.Perfil
    )

    NavigationBar  {
        val currentBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}