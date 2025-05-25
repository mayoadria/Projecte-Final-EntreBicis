package cat.copernic.amayo.frontend.recompensaManagment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.Session.SessionViewModel
import cat.copernic.amayo.frontend.core.HeaderReusable
import cat.copernic.amayo.frontend.recompensaManagment.model.Estat
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel

/**
 * Pantalla principal de la gestió de recompenses.
 *
 * Mostra una llista de recompenses disponibles, permet aplicar filtres i ordenar
 * per diferents criteris (descripció, cost). Utilitza el ViewModel per carregar les dades
 * i controlar la sessió de l'usuari.
 *
 * @param llistaViewmodel ViewModel per gestionar la llista de recompenses.
 * @param navController Controlador de navegació per canviar de pantalla.
 * @param sessionViewModel ViewModel de sessió per obtenir dades de l'usuari.
 */
@Composable
fun recompensa(
    llistaViewmodel: llistaViewmodel,
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {

        llistaViewmodel.LlistarRecompenses(context)

    }
    var filtroDesc by remember { mutableStateOf("") }
    var filtroObs by remember { mutableStateOf("") }
    var filtroEstat by remember { mutableStateOf("") }
    var ordenarPor by remember { mutableStateOf("") }
    var ascendente by remember { mutableStateOf(true) }

    // Obtener las recompensas desde el ViewModel
    val allRecompensas by llistaViewmodel.recompesa.collectAsState()

    // Filtrar recompensas según el estado: DISPONIBLES o RESERVADAS
    var filteredRecompensas = allRecompensas.filter { recompensa ->

        recompensa.estat == Estat.DISPONIBLES

                &&
                (filtroDesc.isEmpty() || recompensa.descripcio?.contains(
                    filtroDesc,
                    ignoreCase = true
                ) == true) &&
                (filtroObs.isEmpty() || recompensa.observacions?.contains(
                    filtroObs,
                    ignoreCase = true
                ) == true) &&
                (filtroEstat.isEmpty() || recompensa.estat?.name?.contains(
                    filtroEstat,
                    ignoreCase = true
                ) == true)
    }

    // Aplicar ordenación
    filteredRecompensas = when (ordenarPor) {
        "descripcio" -> if (ascendente) filteredRecompensas.sortedBy { it.descripcio } else filteredRecompensas.sortedByDescending { it.descripcio }
        "cost" -> if (ascendente) filteredRecompensas.sortedBy { it.cost } else filteredRecompensas.sortedByDescending { it.cost }
        else -> filteredRecompensas
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x9C9CF3FF))
            .statusBarsPadding()
    ) {
        HeaderReusable(
            title = "Premis",
            onFilterApplied = { desc, obs, estat ->
                filtroDesc = desc
                filtroObs = obs
                filtroEstat = estat
            },
            onSortSelected = { criterio, asc ->
                ordenarPor = criterio
                ascendente = asc
            },
            sessionViewModel = sessionViewModel
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredRecompensas) { recompensa ->

                RecompensaItem(recompensa = recompensa, navController, scale = 1f)

            }
        }
    }
}



