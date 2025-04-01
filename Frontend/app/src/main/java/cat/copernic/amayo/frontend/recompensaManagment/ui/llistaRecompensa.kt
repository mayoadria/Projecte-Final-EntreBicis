package cat.copernic.amayo.frontend.recompensaManagment.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import cat.copernic.amayo.frontend.recompensaManagment.viewmodels.llistaViewmodel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun recompensa(llistaViewmodel: llistaViewmodel,navController: NavController) {
    var filtroDesc by remember { mutableStateOf("") }
    var filtroObs by remember { mutableStateOf("") }
    var filtroEstat by remember { mutableStateOf("") }
    var ordenarPor by remember { mutableStateOf("") }
    var ascendente by remember { mutableStateOf(true) }

    val allRecompensas by llistaViewmodel.recompesa.collectAsState()

    // Aplicar filtros a la lista de recompensas
    var filteredRecompensas = allRecompensas.filter { recompensa ->
        (filtroDesc.isEmpty() || recompensa.descripcio?.contains(filtroDesc, ignoreCase = true) == true) &&
                (filtroObs.isEmpty() || recompensa.observacions?.contains(filtroObs, ignoreCase = true) == true) &&
                (filtroEstat.isEmpty() || recompensa.estat?.name?.contains(filtroEstat, ignoreCase = true) == true)
    }

    // Aplicar ordenación
    filteredRecompensas = when (ordenarPor) {
        "descripcio" -> if (ascendente) filteredRecompensas.sortedBy { it.descripcio } else filteredRecompensas.sortedByDescending { it.descripcio }
        "cost" -> if (ascendente) filteredRecompensas.sortedBy { it.cost } else filteredRecompensas.sortedByDescending { it.cost }
        else -> filteredRecompensas
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
        Header(
            onFilterApplied = { desc, obs, estat ->
                filtroDesc = desc
                filtroObs = obs
                filtroEstat = estat
            },
            onSortSelected = { criterio, asc ->
                ordenarPor = criterio
                ascendente = asc
            }
        )

        filteredRecompensas.forEach { recompensas ->
            RecompensaItem(recompensa = recompensas, navController,scale = 1f)
        }
    }
}

@Composable
fun Header(onFilterApplied: (String, String, String) -> Unit, onSortSelected: (String, Boolean) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menú",
                        tint = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Filtres") },
                        onClick = {
                            expanded = false
                            showFilterDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ordenar") },
                        onClick = {
                            expanded = false
                            showSortDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            onApplyFilter = { desc, obs, cost, estat ->
                onFilterApplied(desc, obs, estat)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    if (showSortDialog) {
        SortDialog(
            onSortSelected = { criterio, asc ->
                onSortSelected(criterio, asc)
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }
}

@Composable
fun FilterDialog(
    onApplyFilter: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var filterDesc by remember { mutableStateOf("") }
    var filterObs by remember { mutableStateOf("") }
    var filterCost by remember { mutableStateOf("") }
    var filterEstat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona els filtres") },
        text = {
            Column {
                TextField(
                    value = filterDesc,
                    onValueChange = { filterDesc = it },
                    placeholder = { Text("Introdueix una descripció") }
                )

                TextField(
                    value = filterObs,
                    onValueChange = { filterObs = it },
                    placeholder = { Text("Introdueix observacions") }
                )

                TextField(
                    value = filterCost,
                    onValueChange = { filterCost = it },
                    placeholder = { Text("Introdueix un cost") }
                )

                TextField(
                    value = filterEstat,
                    onValueChange = { filterEstat = it },
                    placeholder = { Text("Introdueix un estat") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onApplyFilter(filterDesc, filterObs, filterCost, filterEstat) }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
@Composable
fun SortDialog(
    onSortSelected: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona l'ordre") },
        text = {
            Column {
                Button(onClick = { onSortSelected("descripcio", true) }) {
                    Text("Ordenar A-Z")
                }
                Button(onClick = { onSortSelected("descripcio", false) }) {
                    Text("Ordenar Z-A")
                }
                Button(onClick = { onSortSelected("cost", true) }) {
                    Text("Ordenar Cost Asc.")
                }
                Button(onClick = { onSortSelected("cost", false) }) {
                    Text("Ordenar Cost Desc.")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}