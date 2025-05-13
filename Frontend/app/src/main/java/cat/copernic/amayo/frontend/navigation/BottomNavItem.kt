package cat.copernic.amayo.frontend.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Classe segellada que representa un element de la barra de navegació inferior.
 *
 * Cada objecte d'aquesta classe defineix una opció de navegació amb la seva ruta,
 * icona i etiqueta associada. Es fa servir per construir la BottomNavigationBar de l'aplicació.
 *
 * @property route Ruta de navegació associada a l'element.
 * @property icon Icona que es mostrarà a la barra inferior.
 * @property label Text descriptiu de l'element.
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    /** Element de navegació per a la pantalla d'inici. */
    object Inici : BottomNavItem("inici", Icons.Default.Home, "Inici")

    /** Element de navegació per a la gestió de recompenses. */
    object Rec : BottomNavItem("recompensa", Icons.Default.ShoppingCart, "Recompenses")

    /** Element de navegació per al perfil de l'usuari. */
    object Perfil : BottomNavItem("perfil", Icons.Default.AccountCircle, "Jo")
}