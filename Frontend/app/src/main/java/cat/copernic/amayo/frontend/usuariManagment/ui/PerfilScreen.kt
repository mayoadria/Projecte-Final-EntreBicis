package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
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
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.recompensaManagment.ui.decodeBase64ToBitmap
import coil.compose.rememberAsyncImagePainter

@Composable
fun perfil(sessionViewModel: SessionViewModel){
    val nom by sessionViewModel.userData.collectAsState()
    val backgroundColor = Color(0xFF89EAF7) // azul claro
    val iconColor = Color.Black
    val nombreUsuario = "${nom?.nom}"
    val saldo = "${nom?.saldo}"
    val bitmap = remember(nom?.foto) { nom?.foto?.let { decodeBase64ToBitmap(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                contentDescription = "Imagen de la recompensa",
                modifier = Modifier
                    .fillMaxWidth() // Ajusta el ancho al contenedor
                    .height(200.dp) // Cambia el alto de la imagen
                    .background(Color(0x009CF3FF), shape = RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de acción
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButtonWithText(icon = Icons.Default.Edit, text = "Edita", iconColor = iconColor)
                IconButtonWithText(icon = Icons.Default.Star, text = "Rutes", iconColor = iconColor)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButtonWithText(icon = Icons.Default.ShoppingCart, text = "Recompenses", iconColor = iconColor)
                IconButtonWithText(icon = Icons.Default.ExitToApp, text = "Sortir", iconColor = iconColor)
            }
        }
    }
}

@Composable
fun IconButtonWithText(icon: ImageVector, text: String, iconColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF55D9F2), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
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
