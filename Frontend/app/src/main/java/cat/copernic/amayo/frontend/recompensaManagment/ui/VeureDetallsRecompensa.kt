package cat.copernic.amayo.frontend.recompensaManagment.ui

import android.widget.Toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun detalls(
    viewmodel: llistaViewmodel,
    recompensaId: Long,
    reservaViewmodel: ReservaViewmodel,
    sessionViewModel: SessionViewModel,
    modificarViewModel: ModificarViewModel,
    navController: NavController,
) {
    val nom by sessionViewModel.userData.collectAsState()
    val recompensa = viewmodel.recompensaD.value
    val bitmap = remember(recompensa?.foto) {
        recompensa?.foto?.let { decodeBase64ToBitmap(it) }
    }
    LaunchedEffect(recompensaId) {
        viewmodel.listar(recompensaId)
    }

    val context = LocalContext.current
    val puedeReservar =
        nom != null && recompensa != null && nom!!.saldo!! >= recompensa.cost!! && !nom!!.reserva
    val tieneReservas = nom != null && recompensa != null && !nom!!.reserva
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Azul claro
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detalls del Premi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
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
                    Text(
                        text = it,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
                recompensa?.cost?.let {
                    Text(
                        text = "$it Punts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                recompensa?.observacions?.let {
                    Text(text = it, fontSize = 16.sp, color = Color(0xFF757575))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Text(
                        "Punt d'intercanvi:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0288D1)
                    )
                    recompensa?.puntBescanviId?.nom?.let {
                        Text(
                            it,
                            fontSize = 16.sp,
                            color = Color(0xFF424242)
                        )
                    }
                    recompensa?.puntBescanviId?.adreca?.let {
                        Text(
                            it,
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    recompensa?.puntBescanviId?.personaContacte?.let {
                        Text(
                            "Contacto: $it",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                    recompensa?.puntBescanviId?.telefon?.let {
                        Text(
                            "Tel: $it",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Imagen de la recompensa",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Ajustar tamaño
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            )
        }
        Spacer(modifier = Modifier.height(16.dp))



            // BOTÓN DE RESERVAR (TU BLOQUE ACTUAL ADAPTADO)
            Button(
                onClick = {
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val fecha = LocalDateTime.now().format(formatter)
                    if (tieneReservas) {
                        if (puedeReservar) {
                            nom?.let { usuario ->
                                usuario.reserva = true

                                modificarViewModel.updateClient(
                                    client = usuario,
                                    contentResolver = navController.context.contentResolver
                                )
                            }

                            recompensa?.let { recompensa ->
                                recompensa.estat = Estat.RESERVADES
                                recompensa.usuariRecompensa = nom
                                recompensa.dataReserva = fecha
                                viewmodel.updateRecompensa(
                                    client = recompensa,
                                    contentResolver = navController.context.contentResolver
                                )
                            }


                            val nuevaReserva = Reserva(
                                id = null,
                                emailUsuari = nom,
                                idRecompensa = recompensa,
                                datareserva = fecha,
                                estat = EstatReserva.RESERVADA
                            )

                            reservaViewmodel.crearReserva(nuevaReserva)

                        } else {
                            Toast.makeText(context, "Saldo insuficient.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Límit màxim de reserves obtingut.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF0288D1)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reservar", color = Color.White, fontSize = 18.sp)
            }

    }
}
