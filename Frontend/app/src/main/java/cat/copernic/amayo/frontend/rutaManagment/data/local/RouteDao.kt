package cat.copernic.amayo.frontend.rutaManagment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


/**
 * Interfaz DAO (Data Access Object) para realizar operaciones en la base de datos relacionadas con las rutas y las posiciones geográficas.
 * Define métodos para insertar, consultar y eliminar rutas y posiciones asociadas a esas rutas.
 *
 * **Operaciones disponibles**:
 * - Insertar rutas.
 * - Insertar posiciones asociadas a una ruta.
 * - Obtener todas las posiciones asociadas a una ruta específica.
 * - Eliminar todas las posiciones de una ruta.
 * - Eliminar una ruta específica.
 */
@Dao
interface RouteDao {
    /**
     * Inserta una nueva ruta en la base de datos.
     *
     * @param route Objeto de tipo [RouteEntity] que representa la ruta a insertar.
     * @return El identificador único de la ruta insertada.
     */
    @Insert
    suspend fun insertRoute(route: RouteEntity): Long
    /**
     * Inserta una nueva posición asociada a una ruta en la base de datos.
     *
     * @param pos Objeto de tipo [PositionEntity] que representa la posición geográfica a insertar.
     */
    @Insert
    suspend fun insertPosition(pos: PositionEntity)
    /**
     * Obtiene todas las posiciones asociadas a una ruta específica.
     * Las posiciones se ordenan por el identificador de la posición.
     *
     * @param routeId El identificador único de la ruta de la cual se obtendrán las posiciones.
     * @return Una lista de objetos [PositionEntity] que representan las posiciones asociadas a la ruta.
     */
    @Query("SELECT * FROM positions WHERE routeId = :routeId ORDER BY id")
    suspend fun getPositionsForRoute(routeId: Long): List<PositionEntity>
    /**
     * Elimina todas las posiciones asociadas a una ruta específica.
     *
     * @param routeId El identificador único de la ruta cuyas posiciones se eliminarán.
     */
    @Query("DELETE FROM positions WHERE routeId = :routeId")
    suspend fun deletePositionsForRoute(routeId: Long)
    /**
     * Elimina una ruta específica de la base de datos.
     *
     * @param routeId El identificador único de la ruta que se desea eliminar.
     */
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRoute(routeId: Long)
}
