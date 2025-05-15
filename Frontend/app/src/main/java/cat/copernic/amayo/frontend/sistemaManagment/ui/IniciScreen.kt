package cat.copernic.amayo.frontend.sistemaManagment.ui

import android.app.DatePickerDialog
import android.app.Application
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.Operator
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.RutesViewmodel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun inici(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    val ctx = LocalContext.current
    val user by sessionViewModel.userData.collectAsState()
    val email = user?.email

    val routesVM: RutesViewmodel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(ctx.applicationContext as Application)
    )
    val routes by remember { derivedStateOf { routesVM.routes } }

    // Carga inicial
    LaunchedEffect(email) {
        email?.let { routesVM.loadRoutes(it) }
    }

    // Mostrar/ocultar filtros
    var showFilters by remember { mutableStateOf(false) }

    // Scroll & FAB
    val listState = rememberLazyListState()
    var prevOffset by remember { mutableStateOf(0) }
    var fabVisible by remember { mutableStateOf(true) }
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex * 1_000_000 +
                    listState.firstVisibleItemScrollOffset to listState.isScrollInProgress
        }.collect { (offset, scrolling) ->
            val down = offset > prevOffset
            fabVisible = !(scrolling && down)
            prevOffset = offset
        }
    }

    // Detalles
    var selected by remember { mutableStateOf<RutaApi.RutaDto?>(null) }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                exit  = fadeOut(tween(300)) + scaleOut(tween(300))
            ) {
                FloatingActionButton(onClick = { navController.navigate("rutas") }) {
                    Icon(Icons.Default.Add, contentDescription = "Iniciar ruta")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header filtros
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { showFilters = !showFilters }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(
                    if (showFilters) "Ocultar filtros" else "Mostrar filtros",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Panel filtros
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit  = shrinkVertically(tween(300)) + fadeOut(tween(300))
            ) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        // — Estado
                        Text("Estado", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val estados = listOf<RutaApi.EstatRutes?>(null) + RutaApi.EstatRutes.values()
                            estados.forEach { est ->
                                FilterChip(
                                    selected = routesVM.filtroEstado == est,
                                    onClick = {
                                        routesVM.filtroEstado = est
                                        routesVM.applyFilters()
                                    },
                                    label = { Text(est?.name ?: "Todas") }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // — Fecha creación
                        val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
                        Text("Fecha creación", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Desde" to routesVM.filtroFechaDesde,
                                "Hasta" to routesVM.filtroFechaHasta
                            ).forEach { (label, current) ->
                                TextField(
                                    value = current?.format(dateFormatter) ?: "",
                                    onValueChange = {},
                                    label = { Text(label) },
                                    readOnly = true,
                                    modifier = Modifier.weight(1f),
                                    trailingIcon = {
                                        Row {
                                            if (current != null) {
                                                IconButton(onClick = {
                                                    if (label == "Desde") routesVM.filtroFechaDesde = null
                                                    else                    routesVM.filtroFechaHasta = null
                                                    routesVM.applyFilters()
                                                }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                                                }
                                            }
                                            IconButton(onClick = {
                                                val cal = Calendar.getInstance()
                                                DatePickerDialog(
                                                    ctx,
                                                    { _, y, m, d ->
                                                        val sel = LocalDate.of(y, m + 1, d)
                                                        if (label == "Desde") {
                                                            when {
                                                                sel > LocalDate.now() ->
                                                                    Toast.makeText(ctx, "No puede ser > hoy", Toast.LENGTH_SHORT).show()
                                                                routesVM.filtroFechaHasta?.let { sel > it } == true ->
                                                                    Toast.makeText(ctx, "Hasta debe ≥ Desde", Toast.LENGTH_SHORT).show()
                                                                else -> {
                                                                    routesVM.filtroFechaDesde = sel
                                                                    routesVM.applyFilters()
                                                                }
                                                            }
                                                        } else {
                                                            if (routesVM.filtroFechaDesde?.let { sel < it } == true) {
                                                                Toast.makeText(ctx, "Hasta ≥ Desde", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                routesVM.filtroFechaHasta = sel
                                                                routesVM.applyFilters()
                                                            }
                                                        }
                                                    },
                                                    cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DAY_OF_MONTH)
                                                ).show()
                                            }) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // — Kilómetros
                        Text("Kilómetros (km)", style = MaterialTheme.typography.labelMedium)
                        RangeSlider(
                            value = routesVM.filtroKmRange,
                            onValueChange = {
                                routesVM.filtroKmRange = it
                                routesVM.applyFilters()
                            },
                            valueRange = 0f..50f
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${routesVM.filtroKmRange.start.toInt()} km")
                            Text(
                                if (routesVM.filtroKmRange.endInclusive >= 50f) "sin límite"
                                else "${routesVM.filtroKmRange.endInclusive.toInt()} km"
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // — Tiempo
                        Text("Tiempo", style = MaterialTheme.typography.labelMedium)
                        RangeSlider(
                            value = routesVM.filtroTimeRange,
                            onValueChange = {
                                routesVM.filtroTimeRange = it
                                routesVM.applyFilters()
                            },
                            valueRange = 0f..5f
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            fun Float.toHourMin(): String {
                                val totalMin = (this * 60).roundToInt()
                                val h = totalMin / 60
                                val m = totalMin % 60
                                return "${h}h${m.toString().padStart(2,'0')}"
                            }
                            Text(routesVM.filtroTimeRange.start.toHourMin())
                            Text(
                                if (routesVM.filtroTimeRange.endInclusive >= 5f) "sin límite"
                                else routesVM.filtroTimeRange.endInclusive.toHourMin()
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // — Vel. media
                        Text("Vel. media (km/h)", style = MaterialTheme.typography.labelMedium)
                        var velInput by remember { mutableStateOf(routesVM.filtroVelMedia?.second?.toString() ?: "") }
                        val currentOp = routesVM.filtroVelMedia?.first ?: Operator.GREATER

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = velInput,
                                onValueChange = { velInput = it },
                                label = { Text("km/h") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                val v = velInput.toFloatOrNull() ?: 0f
                                routesVM.filtroVelMedia = Operator.GREATER to v
                                routesVM.applyFilters()
                            }) {
                                Text("Más")
                            }
                            Spacer(Modifier.width(4.dp))
                            Button(onClick = {
                                val v = velInput.toFloatOrNull() ?: 0f
                                routesVM.filtroVelMedia = Operator.LESS to v
                                routesVM.applyFilters()
                            }) {
                                Text("Menos")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Limpiar filtros
                        TextButton(
                            onClick = {
                                routesVM.clearFilters()
                                velInput = ""
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            }

            // Lista de rutas o mensaje vacío
            if (routes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay rutas.", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(routes) { ruta ->
                        RouteCard(ruta) { selected = ruta }
                    }
                }
            }
        }
    }

    // Diálogo de detalles
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    ruta.nom,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "%.1f km".format(ruta.km),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onViewDetails(ruta) }) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "Ver detalles",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
