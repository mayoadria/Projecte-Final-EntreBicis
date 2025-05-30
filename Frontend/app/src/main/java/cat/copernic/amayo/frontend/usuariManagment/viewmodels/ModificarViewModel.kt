package cat.copernic.amayo.frontend.usuariManagment.viewmodels

import android.content.ContentResolver
import android.content.Context
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
import cat.copernic.amayo.frontend.core.Logger
import cat.copernic.amayo.frontend.usuariManagment.data.remote.UsuariApi
import cat.copernic.amayo.frontend.usuariManagment.data.repositories.UsuariRetrofitTLSInstance
import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * ViewModel encarregat de gestionar la modificació de dades d'usuari, incloent-hi text i imatge.
 * Gestiona la càrrega d'imatges, conversió a Base64 i actualització via API.
 */
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

    /**
     * Actualitza la URI de la imatge seleccionada.
     * @param uri URI de la imatge seleccionada o null si es vol esborrar.
     */
    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }
    /**
     * Converteix una URI d'imatge a un array de bytes.
     * @param uri URI de la imatge a convertir.
     * @param contentResolver ContentResolver per accedir a la imatge.
     * @return Array de bytes de la imatge o null si hi ha error.
     */
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
    /**
     * Carrega un Bitmap a partir d'una URI, compatible amb diferents versions d'Android.
     * @param uri URI de la imatge.
     * @param contentResolver ContentResolver per accedir a la imatge.
     * @return Bitmap resultant o null si hi ha error.
     */
    fun loadBitmapFromUri(uri: Uri, contentResolver: ContentResolver, context: Context): Bitmap? {
        return try {
            Logger.guardarLog(context, "Convertint URI a bytes: $uri")
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


    /**
     * Actualitza les dades d'un usuari enviant la informació (i imatge si cal) a l'API.
     * @param client Usuari amb les dades a modificar.
     * @param contentResolver ContentResolver per accedir a imatges.
     */
    fun updateClient(client: Usuari, contentResolver: ContentResolver,context: Context) {
        viewModelScope.launch {
            try {
                Logger.guardarLog(context, "Iniciant updateClient per ${client.email}")
                // Verificar si se ha actualizado la imagen
                val imageBytes = _selectedImageUri.value?.let { uri ->
                    Logger.guardarLog(context, "Processant imatge nova: $uri")
                    convertUriToByteArray(uri, contentResolver)
                }

                // Convertir a Base64 si hay imagen nueva
                val base64Image = imageBytes?.let {
                    Logger.guardarLog(context, "Convertint imatge a Base64")
                    Base64.encodeToString(it, Base64.DEFAULT) }

                // Asignar la nueva imagen si existe
                if (base64Image != null) {
                    client.foto = base64Image
                    Logger.guardarLog(context, "Afegida imatge codificada al client")
                }

                // Llamada al nuevo endpoint sin clientId separado
                val response = UserApi.update(client)
                if (response.isSuccessful) {
                    Logger.guardarLog(context, "Usuari actualitzat correctament")
                    Log.d("ModificarViewModel", "Usuario actualizado con éxito")
                } else {
                    Logger.guardarLog(context, "Error API: ${response.code()} - ${response.errorBody()?.string()}")
                    Log.e("ModificarViewModel", "Error al actualizar el usuario: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Logger.guardarLog(context, "Excepció updateClient: ${e.message}")
                Log.e("ModificarViewModel", "Excepción al actualizar el usuario: ${e.message}")
            }
        }
    }
}