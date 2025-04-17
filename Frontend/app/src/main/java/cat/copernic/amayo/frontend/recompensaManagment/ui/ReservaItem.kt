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
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva

@Composable
fun ReservaItem(
    reserva: Reserva,
    navController: NavController,
    scale: Float,
    verReservadas: Boolean = false
) {
    val recompensa = reserva.idRecompensa
    val bitmap = remember(recompensa?.foto) {
        recompensa?.foto?.let { decodeBase64ToBitmap(it) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // Aquí podrías navegar a una pantalla de detalles si quieres
                 navController.navigate("detallsReserva/${reserva.id}")
            },
        elevation = CardDefaults.cardElevation(scaledDp(1.dp, scale)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Estado y puntos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4E98DE), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    reserva.estat?.let {
                        Text(
                            text = "Estat: ${it.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    recompensa?.observacions?.let {
                        Text(
                            text = "Observacions: $it",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                recompensa?.cost?.let {
                    Text(
                        text = "$it Punts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Imagen
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imatge de la recompensa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Otros detalles
            reserva.datareserva?.let {
                Text("Data reserva: $it", fontSize = 14.sp)
            }
            Text("Caduca: ${if (reserva.caducada == true) "Sí" else "No"}", fontSize = 14.sp)
            reserva.emailUsuari?.email?.let {
                Text("Reservat per: $it", fontSize = 14.sp)
            }
            recompensa?.descripcio?.let {
                Text("Recompensa: $it", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}


