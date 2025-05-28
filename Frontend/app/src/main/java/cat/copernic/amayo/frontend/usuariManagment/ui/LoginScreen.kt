package cat.copernic.amayo.frontend.usuariManagment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.Session.SessionUser
import cat.copernic.amayo.frontend.Session.SessionViewModel
import cat.copernic.amayo.frontend.usuariManagment.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión que permite al usuario introducir su correo y contraseña
 * para autenticarse. Gestiona la validación de campos, errores de autenticación
 * y navegación tras un login exitoso.
 *
 * @param navController Controlador de navegación para gestionar cambios de pantalla.
 * @param viewModel ViewModel encargado de la lógica de autenticación de usuario.
 * @param sessionViewModel ViewModel para gestionar la sesión activa del usuario.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    sessionViewModel: SessionViewModel
) {
    val isUserLoged by viewModel.isUserLoged.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.contra.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Observamos el estado del usuario y error del ViewModel
    val usuari by viewModel.usuari.collectAsState()
    //val error by viewModel.error.collectAsState()
    val emptyEmailError by viewModel.emptyEmailError.collectAsState()
    val emptyContraError by viewModel.emptyContraError.collectAsState()
    val emailNotFoundError by viewModel.emailNotFoundError.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val context = LocalContext.current
    // Si el usuario es distinto de null, se ha iniciado sesión correctamente
    LaunchedEffect(isUserLoged) {
        if (isUserLoged) {
            sessionViewModel.updateSession(
                SessionUser(email, true), context
            )
            navController.navigate("inici") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.resetUserLoged()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3E41)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF64E5FF), shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
                .width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LOGIN", fontSize = 24.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                isError = emptyEmailError != null || authError != null || emailNotFoundError != null,
                label = { Text("Correu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            emptyEmailError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            emailNotFoundError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            authError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.updateContra(it) },
                isError = emptyContraError != null,
                label = { Text("Contrasenya") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Face else Icons.Default.Clear,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            emptyContraError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }




            Spacer(modifier = Modifier.height(16.dp))

            // Botón Entrar: llama al login del ViewModel
            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        val user = viewModel.login(context)
                        sessionViewModel.updateUserData(user)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00CFFF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Entrar", color = Color.White)
            }

            TextButton(
                onClick = { navController.navigate("changePass") },
                shape = RoundedCornerShape(50),          // mantiene la pulsación redondeada
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),                      // alto táctil accesible
                colors = ButtonDefaults.textButtonColors( // colores solo para el texto
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Canviar Contrasenya",
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
