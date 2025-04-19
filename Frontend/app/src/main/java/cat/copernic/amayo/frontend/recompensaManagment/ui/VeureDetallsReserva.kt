package cat.copernic.amayo.frontend.recompensaManagment.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.recompensaManagment.model.Estat
import cat.copernic.amayo.frontend.recompensaManagment.model.EstatReserva
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel

@Composable
fun DetallsReserva(
    reservaId: Long,
    reservaViewmodel: ReservaViewmodel,
    llistaViewmodel: llistaViewmodel,
    sessionViewModel: SessionViewModel,
    modificarViewModel: ModificarViewModel,
    navController: NavController
) {
    val nom by sessionViewModel.userData.collectAsState()
    val context = LocalContext.current
    val reserva = reservaViewmodel.reservaD.value
    val recompensa = reserva?.idRecompensa
    val bitmap = remember(recompensa?.foto) {
        recompensa?.foto?.let { decodeBase64ToBitmap(it) }
    }

    LaunchedEffect(reservaId) {
        reservaViewmodel.listar(reservaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detalls de la Reserva",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0288D1)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Estat: ${reserva?.estat?.name ?: "Desconegut"}", fontSize = 18.sp)
                Text("Data reserva: ${reserva?.datareserva ?: "No disponible"}", fontSize = 16.sp)
                Text("Caducada: ${if (reserva?.caducada == true) "Sí" else "No"}", fontSize = 16.sp)
                reserva?.emailUsuari?.email?.let {
                    Text("Reservat per: $it", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Informació de la Recompensa",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0288D1)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                recompensa?.descripcio?.let {
                    Text("Descripció: $it", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                recompensa?.cost?.let {
                    Text("Cost: $it punts", fontSize = 16.sp)
                }
                recompensa?.observacions?.let {
                    Text("Observacions: $it", fontSize = 14.sp)
                }
                recompensa?.puntBescanviId?.let { punt ->
                    Text("Punt d'intercanvi: ${punt.nom}", fontSize = 14.sp)
                    Text("Adreça: ${punt.adreca}", fontSize = 14.sp)
                    Text("Contacte: ${punt.personaContacte}", fontSize = 14.sp)
                    Text("Telèfon: ${punt.telefon}", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Imatge de la recompensa",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para pasar de ASSIGNADES a PER_RECOLLIR
        if (recompensa?.estat == Estat.ASSIGNADES) {
            Button(
                onClick = {
                    nom?.let { usuario ->
                        usuario.reserva = false
                        modificarViewModel.updateClient(
                            client = usuario,
                            contentResolver = navController.context.contentResolver
                        )
                    }
                    reserva?.let {
                        reserva.estat = EstatReserva.PER_RECOLLIR
                        recompensa.estat = Estat.PER_RECOLLIR

                        llistaViewmodel.updateRecompensa(
                            recompensa,
                            contentResolver = navController.context.contentResolver
                        )
                        reservaViewmodel.updateReserva(reserva)

                        Toast.makeText(context, "Recompensa per Recollir!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Passar a Per Recollir", color = Color.White, fontSize = 16.sp)
            }
        }

        // Botón para pasar de PER_RECOLLIR a RECOLLIDA
        if (recompensa?.estat == Estat.PER_RECOLLIR) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    reserva?.let {
                        reserva.estat = EstatReserva.RECOLLIDA
                        recompensa.estat = Estat.RECOLLIDA

                        llistaViewmodel.updateRecompensa(
                            recompensa,
                            contentResolver = navController.context.contentResolver
                        )
                        reservaViewmodel.updateReserva(reserva)

                        Toast.makeText(context, "Recompensa recollida!", Toast.LENGTH_SHORT).show()
                        navController.navigate("check")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Recollir Recompensa", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
