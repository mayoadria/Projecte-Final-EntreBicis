package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.R
import cat.copernic.amayo.frontend.core.auth.SessionViewModel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ModificarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfil(
    sessionViewModel: SessionViewModel,
    modificarViewModel: ModificarViewModel,
    navController: NavController
) {
    val usu by sessionViewModel.userData.collectAsState()
    var nom by remember { mutableStateOf(usu?.nom ?: "") }
    var cognom by remember { mutableStateOf(usu?.cognom ?: "") }
    var telefon by remember { mutableStateOf(usu?.telefon ?: "") }
    var poblacio by remember { mutableStateOf(usu?.poblacio ?: "") }

    val bitmap = remember(usu?.foto) {
        usu?.foto?.let { decodeBase64ToBitmap(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF84F5FF)) // Fondo celeste
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen de perfil
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } ?: Text("No hay imagen disponible.")

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de texto
        TextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nombre") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = cognom,
            onValueChange = { cognom = it },
            label = { Text("Apellido") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = telefon,
            onValueChange = { telefon = it },
            label = { Text("Teléfono") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = poblacio,
            onValueChange = { poblacio = it },
            label = { Text("Población") },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            onClick = {
                usu?.let { usuario ->
                    usuario.nom = nom
                    usuario.cognom = cognom
                    usuario.telefon = telefon
                    usuario.poblacio = poblacio

                    modificarViewModel.updateClient(
                        client = usuario,
                        contentResolver = navController.context.contentResolver
                    )

                    navController.navigate("perfil")
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Guardar", fontWeight = FontWeight.Bold)
        }
    }
}
