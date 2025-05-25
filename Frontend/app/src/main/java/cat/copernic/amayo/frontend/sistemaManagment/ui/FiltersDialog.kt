package cat.copernic.amayo.frontend.sistemaManagment.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.Operator
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.RutesViewmodel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialog(
    routesVM: RutesViewmodel,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    val displayFmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    var velInput by remember { mutableStateOf(routesVM.filtroVelMedia?.second?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtres de rutes") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(end = 8.dp)
            ) {
                // — Estado
                Text("Estat", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf<RutaApi.EstatRutes?>(null)
                        .plus(RutaApi.EstatRutes.values().toList())
                        .forEach { est ->
                            FilterChip(
                                selected = routesVM.filtroEstado == est,
                                onClick = {
                                    routesVM.filtroEstado = est
                                    routesVM.applyFilters()
                                },
                                label = { Text(est?.name ?: "Totes") }
                            )
                        }
                }

                Spacer(Modifier.height(12.dp))

                // — Fecha creación
                Text("Data creació", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Desde
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                ctx,
                                { _, y, m, d ->
                                    routesVM.filtroFechaDesde = LocalDate.of(y, m + 1, d)
                                    routesVM.applyFilters()
                                },
                                cal.get(Calendar.YEAR),
                                calw.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = routesVM.filtroFechaDesde?.format(displayFmt) ?: "Des de",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (routesVM.filtroFechaDesde != null) {
                        IconButton(onClick = {
                            routesVM.filtroFechaDesde = null
                            routesVM.applyFilters()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar Desde")
                        }
                    }

                    // Hasta
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                ctx,
                                { _, y, m, d ->
                                    routesVM.filtroFechaHasta = LocalDate.of(y, m + 1, d)
                                    routesVM.applyFilters()
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = routesVM.filtroFechaHasta?.format(displayFmt) ?: "Fins",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (routesVM.filtroFechaHasta != null) {
                        IconButton(onClick = {
                            routesVM.filtroFechaHasta = null
                            routesVM.applyFilters()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar Hasta")
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // — Kilómetros
                Text("Quilòmetres (km)", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                RangeSlider(
                    value = routesVM.filtroKmRange,
                    onValueChange = {
                        routesVM.filtroKmRange = it
                        routesVM.applyFilters()
                    },
                    valueRange = 0f..100f
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${routesVM.filtroKmRange.start.toInt()} km")
                    Text(
                        if (routesVM.filtroKmRange.endInclusive >= 100f) "sense límit"
                        else "${routesVM.filtroKmRange.endInclusive.toInt()} km"
                    )
                }

                Spacer(Modifier.height(12.dp))

                // — Tiempo (h)
                Text("Temps (h)", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                RangeSlider(
                    value = routesVM.filtroTimeRange,
                    onValueChange = {
                        routesVM.filtroTimeRange = it
                        routesVM.applyFilters()
                    },
                    valueRange = 0f..10f
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    fun Float.toHourMin(): String {
                        val tm = (this * 60).roundToInt()
                        return "${tm/60}h${(tm%60).toString().padStart(2,'0')}"
                    }
                    Text(routesVM.filtroTimeRange.start.toHourMin())
                    Text(
                        if (routesVM.filtroTimeRange.endInclusive >= 10f) "sense límit"
                        else routesVM.filtroTimeRange.endInclusive.toHourMin()
                    )
                }

                Spacer(Modifier.height(12.dp))

                // — Velocidad media
                Text("Velocitat mitja (km/h)", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = velInput,
                        onValueChange = { velInput = it },
                        label = { Text("km/h") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        velInput.toFloatOrNull()?.let {
                            routesVM.filtroVelMedia = Operator.GREATER to it
                            routesVM.applyFilters()
                        }
                    }) { Text("Més") }
                    Spacer(Modifier.width(4.dp))
                    Button(onClick = {
                        velInput.toFloatOrNull()?.let {
                            routesVM.filtroVelMedia = Operator.LESS to it
                            routesVM.applyFilters()
                        }
                    }) { Text("Menys") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tancar") }
        }
    )
}
