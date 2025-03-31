package cat.copernic.amayo.frontend.recompensaManagment.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun recompensa(llistaViewmodel: llistaViewmodel){
    val recompensa by llistaViewmodel.recompesa.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Header()
        recompensa.forEach { recompensas ->
            RecompensaItem(recompensa = recompensas, scale = 1f)
        }
    }
}

@Composable
fun Header() {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF64D8FF))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Premi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("658,54B", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Nom")
            }
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Punts")
            }
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Comerç")
            }
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Més Recent")
            }
        }
    }
}
