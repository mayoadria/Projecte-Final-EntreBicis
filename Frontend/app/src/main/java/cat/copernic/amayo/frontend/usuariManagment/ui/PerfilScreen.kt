package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cat.copernic.amayo.frontend.core.auth.SessionViewModel

@Composable
fun perfil(sessionViewModel: SessionViewModel){
    val nom by sessionViewModel.userData.collectAsState()
    Text("Nom: $nom")
}