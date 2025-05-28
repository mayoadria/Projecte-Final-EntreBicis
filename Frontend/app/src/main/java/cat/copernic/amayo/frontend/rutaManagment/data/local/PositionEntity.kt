package cat.copernic.amayo.frontend.rutaManagment.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa una entidad de posición en la base de datos. Cada instancia de `PositionEntity`
 * está asociada a una ruta específica mediante la columna `routeId`.
 * Esta entidad se utiliza para almacenar información de las posiciones geográficas de una ruta,
 * incluyendo la latitud, longitud y el timestamp de la posición.
 *
 * @property id Identificador único de la posición. Es una clave primaria que se genera automáticamente.
 * @property routeId Identificador de la ruta a la que pertenece esta posición. Es una clave foránea que hace referencia a `RouteEntity`.
 * @property latitude Latitud de la posición geográfica.
 * @property longitude Longitud de la posición geográfica.
 * @property timestamp Momento en que se registró la posición, expresado en milisegundos desde la época (Unix timestamp).
 *
 * **Relaciones**:
 * - La entidad `PositionEntity` está relacionada con `RouteEntity` mediante el campo `routeId`,
 *   donde se establece una clave foránea a la tabla `routes` con la columna `id` de `RouteEntity`.
 *   Cuando una ruta es eliminada, las posiciones asociadas también serán eliminadas debido a la restricción de eliminación en cascada.
 *
 * **Índices**:
 * - Se crea un índice en el campo `routeId` para mejorar la eficiencia de las consultas que busquen posiciones por ruta.
 */
@Entity(
    tableName = "positions",
    foreignKeys = [ForeignKey(
        entity = RouteEntity::class,
        parentColumns = ["id"],
        childColumns  = ["routeId"],
        onDelete       = ForeignKey.CASCADE
    )],
    indices = [Index("routeId")]
)
data class PositionEntity(
    /**
     * Identificador único de la posición.
     * Es la clave primaria y su valor se genera automáticamente.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Identificador de la ruta a la que pertenece esta posición.
     * Este campo es una clave foránea que hace referencia a la tabla `routes`.
     */
    val routeId: Long,

    /**
     * Latitud de la posición geográfica.
     */
    val latitude: Double,

    /**
     * Longitud de la posición geográfica.
     */
    val longitude: Double,

    /**
     * Timestamp de la posición, representado como el número de milisegundos desde la época (Unix timestamp).
     */
    val timestamp: Long
)
