package cat.copernic.amayo.frontend.sistemaManagment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.core.auth.SessionViewModel

@Composable
fun inici(navController: NavController, sessionViewModel: SessionViewModel) {
    val nom by sessionViewModel.userData.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("rutas") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar ruta")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Nom: ${nom?.nom}")
        }
    }
}
