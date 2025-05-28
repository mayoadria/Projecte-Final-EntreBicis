package cat.copernic.amayo.frontend.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Funció auxiliar que calcula un valor Dp escalat en funció d'un factor de multiplicació.
 *
 * @param value Valor original en Dp.
 * @param scale Factor d'escala a aplicar.
 * @return Valor en Dp després d'aplicar l'escala.
 */
@Composable
fun scaledDp(value: Dp, scale: Float): Dp {
    val density = LocalDensity.current
    return with(density) { (value.toPx() * scale).toDp() }
}

/**
 * Funció per descomprimir una imatge codificada en Base64 i convertir-la en Bitmap.
 *
 * @param base64String Cadena Base64 que representa la imatge.
 * @return L'objecte Bitmap corresponent o null si la descompressió falla.
 */
fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}