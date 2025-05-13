package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.ChangePasswordViewModel

/**
 * Pantalla de cambio de contraseña donde el usuario puede solicitar un email de confirmación,
 * introducir un código de verificación y actualizar su contraseña.
 *
 * @param viewModel ViewModel encargado de gestionar la lógica del cambio de contraseña.
 * @param navController Controlador de navegación para gestionar transiciones entre pantallas.
 */
@Composable
fun ChangePasswordScreen(viewModel: ChangePasswordViewModel, navController: NavController) {
    val context = LocalContext.current

    val isUserPassUpdated by viewModel.isUserPassUpdated.collectAsState()

    LaunchedEffect(isUserPassUpdated) {
        if (isUserPassUpdated) {}
    }

    val email by viewModel.email.collectAsState()
    val codeAuth by viewModel.codeAuth.collectAsState()
    val newContra by viewModel.newContra.collectAsState()
    val repNewContra by viewModel.repNewContra.collectAsState()

    val emailSuccess by viewModel.emailSuccess.collectAsState()
    val emailNotFound by viewModel.emailNotFoundError.collectAsState()

    val emptyEmailError by viewModel.emptyEmailError.collectAsState()
    val emptyCodeAuthError by viewModel.emptyCodeAuthError.collectAsState()
    val emptyNewContraError by viewModel.emptyNewContraError.collectAsState()
    val emptyRepNewContraError by viewModel.emptyRepNewContraError.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val codeError by viewModel.codeError.collectAsState()
    val contraError by viewModel.contraError.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUserPassUpdated) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Canvi de contrasenya",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Email",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .align(Alignment.Start)
                                )
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { viewModel.updateEmail(it) },
                                    isError = emailError != null || emptyEmailError != null || emailNotFound != null,
                                    shape = RoundedCornerShape(50.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 10.dp)
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        disabledContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Gray
                                    )
                                )
                                emptyEmailError?.let {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                                emailError?.let {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                                emailNotFound?.let {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                                emailSuccess?.let {
                                    Text(
                                        text = it,
                                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            }

                            Button(onClick = { viewModel.enviarEmail() }) {
                                Text("Enviar")
                            }
                        }
                    }

                    emailSuccess?.let {
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray)
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = "Codi confirmació",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                OutlinedTextField(
                                    value = codeAuth,
                                    onValueChange = { if (it.length <= 8) viewModel.updateCode(it) },
                                    isError = codeError != null || emptyCodeAuthError != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        disabledContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Gray
                                    )
                                )
                            }

                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = "Nova contrasenya",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                OutlinedTextField(
                                    value = newContra,
                                    onValueChange = { viewModel.updateNewContra(it) },
                                    isError = contraError != null || emptyNewContraError != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        disabledContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Gray
                                    )
                                )

                            }

                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = "Repeteix nova contrasenya",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                OutlinedTextField(
                                    value = repNewContra,
                                    onValueChange = { viewModel.updateRepNewContra(it) },
                                    isError = emptyRepNewContraError != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        disabledContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Gray
                                    )
                                )

                            }

                            Button(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp),
                                onClick = {
                                    viewModel.changePass()
                                }
                            ) {
                                Text("Canviar contrasenya")
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Contrasenya modificada amb èxit",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 5.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Sortir",
                    modifier = Modifier.clickable {
                        navController.navigate("splash") {
                            popUpTo("changePass") { inclusive = true }
                        }
                        viewModel.resetUserPassUpdated()
                    }
                )
            }
        }
    }
}
