package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
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

    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> modificarViewModel.updateSelectedImage(uri) }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFFB8F2FF), Color(0xFFE0FCFF))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val selectedImageUri by modificarViewModel.selectedImageUri.collectAsState()
                val updatedBitmap = remember(selectedImageUri) {
                    selectedImageUri?.let { uri -> modificarViewModel.loadBitmapFromUri(uri, navController.context.contentResolver) }
                        ?: bitmap
                }

// Imagen de perfil clicable
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(2.dp, Color(0xFF0099CC), CircleShape)
                        .clickable {
                            singleImagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    updatedBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Sin imagen",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                val campos = listOf(
                    Triple(nom, "Nombre", { value: String -> nom = value }),
                    Triple(cognom, "Apellido", { value: String -> cognom = value }),
                    Triple(telefon, "Teléfono", { value: String -> telefon = value }),
                    Triple(poblacio, "Población", { value: String -> poblacio = value })
                )

                campos.forEach { (valor, etiqueta, setter) ->
                    TextField(
                        value = valor,
                        onValueChange = setter,
                        label = { Text(etiqueta) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0099CC),
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0099CC))
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
