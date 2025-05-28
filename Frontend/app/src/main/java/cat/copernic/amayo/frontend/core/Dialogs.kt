package cat.copernic.amayo.frontend.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Diàleg per aplicar filtres a la llista de recompenses.
 *
 * Permet filtrar per descripció, observacions, cost i estat.
 *
 * @param onApplyFilter Funció que rep els valors introduïts pels filtres.
 * @param onDismiss Funció que es crida per tancar el diàleg sense aplicar filtres.
 */
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
        title = {
            Text(
                text = "Filtres de Recompenses",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = filterDesc,
                    onValueChange = { filterDesc = it },
                    label = { Text("Descripció") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = filterObs,
                    onValueChange = { filterObs = it },
                    label = { Text("Observacions") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = filterCost,
                    onValueChange = { filterCost = it },
                    label = { Text("Cost") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = filterEstat,
                    onValueChange = { filterEstat = it },
                    label = { Text("Estat") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onApplyFilter(filterDesc, filterObs, filterCost, filterEstat) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Aplicar filtres")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Tancar")
            }
        }
    )
}


/**
 * Diàleg per seleccionar criteris d'ordenació de la llista de recompenses.
 *
 * Permet ordenar per descripció (A-Z, Z-A) i per cost (ascendent, descendent).
 *
 * @param onSortSelected Funció que rep el criteri i l'ordre seleccionat.
 * @param onDismiss Funció per tancar el diàleg.
 */

@Composable
fun SortDialog(
    onSortSelected: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ordenar Recompenses",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Button(
                    onClick = { onSortSelected("descripcio", true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Descripció A-Z")
                }

                Button(
                    onClick = { onSortSelected("descripcio", false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Descripció Z-A")
                }

                Button(
                    onClick = { onSortSelected("cost", true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cost Ascendent")
                }

                Button(
                    onClick = { onSortSelected("cost", false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cost Descendent")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Tancar")
            }
        }
    )
}
