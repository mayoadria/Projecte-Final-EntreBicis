package cat.copernic.amayo.frontend.rutaManagment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una ruta en la base de datos.
 * La entidad está asociada a la tabla `routes` y contiene información básica sobre una ruta,
 * como su nombre, descripción y un identificador único.
 *
 * @property id El identificador único de la ruta. Se genera automáticamente.
 * @property name El nombre de la ruta.
 * @property description Una descripción de la ruta.
 */
@Entity(tableName = "routes")
data class RouteEntity(
    /**
     * Identificador único de la ruta. Se genera automáticamente en la base de datos.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Nombre de la ruta.
     */
    val name: String,

    /**
     * Descripción de la ruta.
     */
    val description: String
)
