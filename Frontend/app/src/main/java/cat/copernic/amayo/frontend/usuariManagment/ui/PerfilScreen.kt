package cat.copernic.amayo.frontend.usuariManagment.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel

/**
 * Pantalla de perfil del usuario que muestra información como nombre, saldo y foto de perfil.
 * Incluye botones para editar perfil, ver rutas, consultar reservas y cerrar sesión.
 *
 * @param sessionViewModel ViewModel encargado de gestionar la sesión del usuario.
 * @param navController Controlador de navegación para gestionar el cambio de pantallas.
 */
@Composable
fun perfil(sessionViewModel: SessionViewModel,navController: NavController) {
    val nom by sessionViewModel.userData.collectAsState()
    val backgroundColor = Color(0xFF89EAF7)
    val iconColor = Color.Black
    val nombreUsuario = "${nom?.nom}"
    val saldo = "${nom?.saldo}"

    // Convertir base64 a Bitmap si existe
    val bitmap = remember(nom?.foto) {
        nom?.foto?.let { decodeBase64ToBitmap(it) }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nombre y saldo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nombreUsuario,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp
            )
            Text(
                text = "$saldo฿",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color(0xFFA1EFFE), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen de perfil
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } ?: Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "Sin imagen",
            tint = Color.Gray,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones con acciones
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                IconButtonWithText(
                    icon = Icons.Default.Edit,
                    text = "Edita",
                    iconColor = iconColor,
                    onClick = {
                        navController.navigate("editar") {
                        }
                    }
                )

                IconButtonWithText(
                    icon = Icons.Default.Star,
                    text = "Rutes",
                    iconColor = iconColor,
                            onClick = {
                        navController.navigate("inici") {
                        }
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                IconButtonWithText(
                    icon = Icons.Default.ShoppingCart,
                    text = "Reserves",
                    iconColor = iconColor,
                    onClick = {
                        navController.navigate("recompensaPropias")
                    }
                )

                IconButtonWithText(
                    icon = Icons.Default.ExitToApp,
                    text = "Sortir",
                    iconColor = iconColor,
                    onClick = {
                        sessionViewModel.logout()
                        navController.navigate("splash") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )

            }
        }
    }
}

/**
 * Componente reutilizable que muestra un icono con texto debajo y responde a clics.
 *
 * @param icon Icono a mostrar.
 * @param text Texto asociado al icono.
 * @param iconColor Color del icono.
 * @param onClick Acción a ejecutar al hacer clic.
 */
@Composable
fun IconButtonWithText(
    icon: ImageVector,
    text: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF55D9F2), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconColor,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Decodifica una cadena en base64 a un objeto Bitmap.
 *
 * @param base64Str Cadena base64 de la imagen.
 * @return Imagen decodificada como Bitmap o null si falla.
 */
fun decodeBase64ToBitmap(base64Str: String): android.graphics.Bitmap? {
    return try {
        val cleanBase64 = base64Str.substringAfter(",") // por si viene con encabezado
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
