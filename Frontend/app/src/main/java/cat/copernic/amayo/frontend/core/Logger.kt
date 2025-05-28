package cat.copernic.amayo.frontend.core

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    fun guardarLog(context: Context, missatge: String) {
        try {
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            Log.d("Logger", "Ruta del log: ${documentsDir?.absolutePath}")

            if (documentsDir != null) {
                // 📅 Nom del fitxer amb la data del dia
                val nomFitxer = "log_" + SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) + ".txt"
                val arxiuLog = File(documentsDir, nomFitxer)

                val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val writer = FileWriter(arxiuLog, true)
                writer.appendLine("[$hora] $missatge")
                writer.flush()
                writer.close()

                Log.d("Logger", "Log escrit correctament a $nomFitxer")
            } else {
                Log.e("Logger", "documentsDir és null!")
            }
        } catch (e: IOException) {
            Log.e("Logger", "Error escrivint log: ${e.message}")
        }
    }
}
