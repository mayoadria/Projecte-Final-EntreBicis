package cat.copernic.amayo.frontend.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Inici : BottomNavItem("inici", Icons.Default.Home, "Inici")
    object Rec : BottomNavItem("recompensa", Icons.Default.ShoppingCart, "Recompenses")
    object Perfil : BottomNavItem("perfil", Icons.Default.AccountCircle, "Jo")
}