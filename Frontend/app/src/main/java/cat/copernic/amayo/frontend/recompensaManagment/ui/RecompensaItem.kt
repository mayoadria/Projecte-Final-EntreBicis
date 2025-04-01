package cat.copernic.amayo.frontend.recompensaManagment.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa


@Composable
fun RecompensaItem(recompensa: Recompensa,navController: NavController, scale: Float) {
    val bitmap = remember(recompensa.foto) {
        recompensa.foto?.let { decodeBase64ToBitmap(it) }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navController.navigate("detalls/${recompensa.id}") },
        elevation = CardDefaults.cardElevation(scaledDp(4.dp, scale))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan)
                    .padding(8.dp)
            ) {
                Column {
                    recompensa.descripcio?.let { Text(text = it, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                    recompensa.observacions?.let { Text(text = it, fontSize = 14.sp) }
                }
                recompensa.cost?.let {
                    Text(
                        text = it.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imagen de la recompensa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Limitar la altura
                )
            }
        }
    }
}

@Composable
fun scaledDp(value: Dp, scale: Float): Dp {
    val density = LocalDensity.current
    return with(density) { (value.toPx() * scale).toDp() }
}

fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}