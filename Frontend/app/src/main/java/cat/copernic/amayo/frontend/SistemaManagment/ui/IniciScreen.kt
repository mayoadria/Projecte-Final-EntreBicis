package cat.copernic.amayo.frontend.SistemaManagment.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cat.copernic.amayo.frontend.core.auth.SessionViewModel

@Composable
fun inici(sessionViewModel:SessionViewModel){
    val nom by sessionViewModel.userData.collectAsState()
    Text("Nom: $nom")
}


