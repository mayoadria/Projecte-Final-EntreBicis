package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitInstance
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ModificarViewModel :ViewModel() {

    private val UserApi: UsuariApi = UsuariRetrofitTLSInstance.retrofitTLSInstance.create(
        UsuariApi::class.java)

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _nom = MutableStateFlow<String?>("")  // Modificado para Sobres
    val nom: StateFlow<String?> = _nom

    private val _cognom = MutableStateFlow<String?>(null)  // Modificado para Sobres
    val cognom: StateFlow<String?> = _cognom

    private val _poblacio = MutableStateFlow<String?>("")  // Modificado para Sobres
    val poblacio: StateFlow<String?> = _poblacio

    private val _telefon = mutableStateOf<String?>("")  // Modificado para Sobres
    val telefon: State<String?> = _telefon
    private val _user = mutableStateOf<Usuari?>(null)  // Modificado para Sobres
    val user: State<Usuari?> = _user


    fun updateNom(nom: String) {
        _nom.value = nom
    }
    fun updateCognom(cognom: String) {
        _cognom.value = cognom
    }
    fun updatePoblacio(poblacio: String) {
        _poblacio.value = poblacio
    }
    fun updateTelefon(telefon: String) {
        _telefon.value = telefon
    }
    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }
    fun convertUriToByteArray(uri: Uri, contentResolver: ContentResolver): ByteArray? {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()

            inputStream?.use { input ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
            }

            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    fun loadBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // Función para actualizar el sobre
    fun updateClient(client: Usuari, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                // Verificar si se ha actualizado la imagen
                val imageBytes = _selectedImageUri.value?.let { uri ->
                    convertUriToByteArray(uri, contentResolver)
                }

                // Convertir a Base64 si hay imagen nueva
                val base64Image = imageBytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }

                // Asignar la nueva imagen si existe
                if (base64Image != null) {
                    client.foto = base64Image
                }

                // Llamada al nuevo endpoint sin clientId separado
                val response = UserApi.update(client)
                if (response.isSuccessful) {
                    Log.d("ModificarViewModel", "Usuario actualizado con éxito")
                } else {
                    Log.e("ModificarViewModel", "Error al actualizar el usuario: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ModificarViewModel", "Excepción al actualizar el usuario: ${e.message}")
            }
        }
    }
}