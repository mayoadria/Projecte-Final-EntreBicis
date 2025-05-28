package cat.copernic.amayo.frontend.rutaManagment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.RutaViewModel

@Composable
fun RouteSummaryDialog(
    stats: RutaViewModel.RouteStats,
    initName: String,
    initDesc: String,
    onSave: (String, String) -> Unit,
    onDiscard: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initName) }
    var desc by rememberSaveable { mutableStateOf(initDesc) }

    val NAME_MAX = 40
    val DESC_MAX = 120

    AlertDialog(
        onDismissRequest = { /* bloqueo intencional */ },
        shape            = RoundedCornerShape(16.dp),
        tonalElevation   = 8.dp,
        containerColor   = MaterialTheme.colorScheme.surface,
        title            = {
            Text(
                "Resum de la ruta",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                // FILA MÉTRICAS 1
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard("Distancia", "%.2f km".format(stats.distKm), Modifier.weight(1f))
                    MetricCard("Temps", formatMillis(stats.totalMillis), Modifier.weight(1f))
                }
                // FILA MÉTRICAS 2
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard("Vel. mitja", "%.2f km/h".format(stats.velMedKmh), Modifier.weight(1f))
                    MetricCard("Ritme", "%.1f min/km".format(stats.ritmeMinKm), Modifier.weight(1f))
                }

                // Nombre de la ruta
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= NAME_MAX) name = it },
                    label = { Text("Nom de la ruta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor          = MaterialTheme.colorScheme.primary,
                        focusedLabelColor    = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(Modifier.height(12.dp))

                // Descripción
                OutlinedTextField(
                    value = desc,
                    onValueChange = { if (it.length <= DESC_MAX) desc = it },
                    label = { Text("Descripció") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp, max = 120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor          = MaterialTheme.colorScheme.primary,
                        focusedLabelColor    = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name.trim(), desc.trim()) }) {
                Text("GUARDAR", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("ESBORRA", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier       = modifier
            .heightIn(min = 64.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        shape          = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

private fun formatMillis(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
