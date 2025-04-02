package cat.copernic.amayo.frontend.recompensaManagment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.copernic.amayo.frontend.R
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel

@Composable
fun detalls(viewmodel: llistaViewmodel, recompensaId: Long) {
    val recompensa = viewmodel.recompensaD.value
    val bitmap = remember(recompensa?.foto) {
        recompensa?.foto?.let { decodeBase64ToBitmap(it) }
    }
    LaunchedEffect(recompensaId) {
        viewmodel.listar(recompensaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF50E5FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detalls Premi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("658,54฿", color = Color.White, modifier = Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                recompensa?.descripcio?.let {
                    Text(
                        it,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                recompensa?.cost?.let {
                    Text(
                        it.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                recompensa?.observacions?.let { Text(it) }
                Spacer(modifier = Modifier.height(8.dp))
                recompensa?.puntBescanviId?.nom?.let { Text(it) }
                recompensa?.puntBescanviId?.adreca?.let { Text(it) }
                Spacer(modifier = Modifier.height(4.dp))
                recompensa?.puntBescanviId?.personaContacte?.let { Text(it) }
                recompensa?.puntBescanviId?.telefon?.let { Text(it) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Imagen de la recompensa",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Limitar la altura
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Acción de reserva */ },
            colors = ButtonDefaults.buttonColors(Color(0xFF50E5FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reserva", color = Color.White, fontSize = 18.sp)
        }
    }
}
