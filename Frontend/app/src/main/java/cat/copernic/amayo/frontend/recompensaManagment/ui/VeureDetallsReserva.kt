package cat.copernic.amayo.frontend.recompensaManagment.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cat.copernic.amayo.frontend.Session.SessionViewModel
import cat.copernic.amayo.frontend.core.decodeBase64ToBitmap
import cat.copernic.amayo.frontend.recompensaManagment.model.Estat
import cat.copernic.amayo.frontend.recompensaManagment.model.EstatReserva
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                Text("Caducada: ${if (reserva?.caducada == true) "Sí" else "No"}", fontSize = 16.sp)
                reserva?.emailUsuari?.email?.let {
                    Text("Reservat per: $it", fontSize = 16.sp)
                }
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("es", "ES"))
                if (recompensa?.estat == Estat.RESERVADES) {
                    recompensa.dataReserva?.let {
                        val fechaReservaFormateada = it.format(formatter)
                        Text("Data de Reserva: $fechaReservaFormateada", fontSize = 18.sp)
                    }
                }
                if (recompensa?.estat == Estat.ASSIGNADES || recompensa?.estat == Estat.PER_RECOLLIR) {
                    recompensa.dataAsignacio?.let {
                        val fechaAssignacioFormateada = it.format(formatter)
                        Text("Data d'assignació: $fechaAssignacioFormateada", fontSize = 18.sp)
                    }
                }
                if (recompensa?.estat == Estat.RECOLLIDA) {
                    recompensa.dataEntrega?.let {
                        val fechaEntregaFormateada = it.format(formatter)
                        Text("Data d'Entrega: $fechaEntregaFormateada", fontSize = 18.sp)
                    }
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
                        recompensa.usuariRecompensa = nom

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

        if (recompensa?.estat == Estat.PER_RECOLLIR) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    reserva?.let {
                        reserva.estat = EstatReserva.RECOLLIDA
                        recompensa.estat = Estat.RECOLLIDA
                        val fecha = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime()
                        recompensa.dataEntrega = fecha
                        recompensa.usuariRecompensa = nom

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

        if (recompensa?.estat == Estat.RESERVADES) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    nom?.let { usuario ->
                        val saldoActual = usuario.saldo ?: 0.0
                        val coste = recompensa?.cost ?: 0
                        sessionViewModel.updateSaldo(saldoActual + coste)

                        usuario.reserva = false
                        modificarViewModel.updateClient(
                            client = usuario,
                            contentResolver = navController.context.contentResolver
                        )
                        sessionViewModel.actualizarReserva(false)
                    }
                    reserva?.let {
                        recompensa.estat = Estat.DISPONIBLES

                        llistaViewmodel.updateRecompensa(
                            recompensa,
                            contentResolver = navController.context.contentResolver
                        )
                        reservaViewmodel.borrar(reservaId)

                        Toast.makeText(context, "Recompensa Cancelada!", Toast.LENGTH_SHORT).show()
                        navController.navigate("recompensaPropias")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar Recompensa", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
