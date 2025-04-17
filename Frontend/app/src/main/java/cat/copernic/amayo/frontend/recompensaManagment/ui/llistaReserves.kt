package cat.copernic.amayo.frontend.recompensaManagment.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.recompensaManagment.model.EstatRecompensa
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.ReservaViewmodel

@Composable
fun reservesPropies(
    reservaViewmodel: ReservaViewmodel,
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    var filtroDesc by remember { mutableStateOf("") }
    var filtroObs by remember { mutableStateOf("") }
    var filtroEstat by remember { mutableStateOf("") }
    var ordenarPor by remember { mutableStateOf("") }
    var ascendente by remember { mutableStateOf(true) }

    val allReservas by reservaViewmodel.reserva.collectAsState()
    val usuari by sessionViewModel.userData.collectAsState()
    val emailActual = usuari?.email

    // Filtrar solo las reservas hechas por el usuario actual
    val filteredRecompensas = allReservas
        .filter { reserva ->
            reserva.emailUsuari?.email == emailActual
        }
        .sortedBy { it.datareserva }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x9C9CF3FF))
            .statusBarsPadding()
    ) {
        Header(
            onFilterApplied = { desc, obs, estat ->
                filtroDesc = desc
                filtroObs = obs
                filtroEstat = estat
            },
            onSortSelected = { criterio, asc ->
                ordenarPor = criterio
                ascendente = asc
            },
            sessionViewModel
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredRecompensas) { recompensa ->
                ReservaItem(reserva = recompensa, navController, scale = 1f)
            }
        }
    }
}
