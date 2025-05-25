package cat.copernic.amayo.frontend.sistemaManagment.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.RutesViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun inici(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    val ctx = LocalContext.current

    // recogemos email y cargamos rutas
    val user by sessionViewModel.userData.collectAsState()
    val email = user?.email
    val routesVM: RutesViewmodel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(ctx.applicationContext as Application)
    )
    val routes by remember { derivedStateOf { routesVM.routes } }
    LaunchedEffect(email) {
        email?.let { routesVM.loadRoutes(it) }
    }

    var showFilters by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<RutaApi.RutaDto?>(null) }

    Scaffold(
        containerColor = Color(0x9C9CF3FF),
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("rutas") }) {
                Icon(Icons.Default.Add, contentDescription = "Iniciar ruta")
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            Column {
                // Botón que abre el popup
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { showFilters = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Filtros", style = MaterialTheme.typography.titleMedium)
                }

                // lista de rutas
                if (routes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay rutas.", textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(
                        state = rememberLazyListState(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(routes) { ruta ->
                            RouteCard(ruta) { selected = ruta }
                        }
                    }
                }
            }

            // Popup de filtros
            if (showFilters) {
                FiltersDialog(
                    routesVM = routesVM,
                    onDismiss = { showFilters = false }
                )
            }
        }
    }

    // Popup de detalles
    selected?.let {
        RouteDetailsDialog(ruta = it) { selected = null }
    }
}

@Composable
private fun RouteCard(
    ruta: RutaApi.RutaDto,
    onViewDetails: (RutaApi.RutaDto) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    ruta.nom,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "%.1f km".format(ruta.km),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(2.dp))
                val secs = ruta.temps.toLong()
                val hh = secs / 3600
                val mm = (secs % 3600) / 60
                val ss = secs % 60
                Text(
                    "%02d:%02d:%02d".format(hh, mm, ss),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = { onViewDetails(ruta) }) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "Ver detalles",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
