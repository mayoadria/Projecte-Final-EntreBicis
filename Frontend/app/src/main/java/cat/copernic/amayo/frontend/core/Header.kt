package cat.copernic.amayo.frontend.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.copernic.amayo.frontend.core.FilterDialog
import cat.copernic.amayo.frontend.core.SortDialog
import cat.copernic.amayo.frontend.Session.SessionViewModel

/**
 * Header reutilitzable per pantalles amb filtres, ordenació i saldo d'usuari.
 *
 * @param title Títol de la pantalla.
 * @param onFilterApplied Funció per aplicar filtres.
 * @param onSortSelected Funció per ordenar.
 * @param sessionViewModel ViewModel per obtenir saldo d'usuari.
 */
@Composable
fun HeaderReusable(
    title: String,
    onFilterApplied: (String, String, String) -> Unit,
    onSortSelected: (String, Boolean) -> Unit,
    sessionViewModel: SessionViewModel
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showFilterDialog by rememberSaveable { mutableStateOf(false) }
    var showSortDialog by rememberSaveable { mutableStateOf(false) }
    val usu by sessionViewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xCE4E98DE))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${usu?.saldo ?: 0} punts",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menú",
                    tint = Color.Black,
                    modifier = Modifier.clickable { expanded = true }
                )

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
