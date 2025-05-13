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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Composable que mostra un element de recompensa en format targeta.
 *
 * Aquesta targeta inclou la descripció, observacions, cost i una imatge codificada en Base64.
 * Quan es fa clic sobre la targeta, es navega a la pantalla de detalls de la recompensa.
 *
 * @param recompensa L'objecte Recompensa a mostrar.
 * @param navController Controlador de navegació per a gestionar canvis de pantalla.
 * @param scale Factor d'escala per a ajustar l'elevació de la targeta.
 */
@Composable
fun RecompensaItem(recompensa: Recompensa, navController: NavController, scale: Float) {
    val bitmap = remember(recompensa.foto) { recompensa.foto?.let { decodeBase64ToBitmap(it) } }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                navController.navigate("detalls/${recompensa.id}")
            },
        elevation = CardDefaults.cardElevation(scaledDp(1.dp, scale)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x9C9CF3FF)) // Azul claro
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCE4E98DE), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    recompensa.descripcio?.let {
                        Text(text = it, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    }
                    recompensa.observacions?.let {
                        Text(text = it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                recompensa.cost?.let {
                    Text(
                        text = "$it Puntos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
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
        }
    }
}
/**
 * Funció auxiliar que calcula un valor Dp escalat en funció d'un factor de multiplicació.
 *
 * @param value Valor original en Dp.
 * @param scale Factor d'escala a aplicar.
 * @return Valor en Dp després d'aplicar l'escala.
 */
@Composable
fun scaledDp(value: Dp, scale: Float): Dp {
    val density = LocalDensity.current
    return with(density) { (value.toPx() * scale).toDp() }
}

/**
 * Funció per descomprimir una imatge codificada en Base64 i convertir-la en Bitmap.
 *
 * @param base64String Cadena Base64 que representa la imatge.
 * @return L'objecte Bitmap corresponent o null si la descompressió falla.
 */
fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}