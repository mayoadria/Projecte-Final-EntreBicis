package cat.copernic.amayo.frontend.rutaManagment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cat.copernic.amayo.frontend.rutaManagment.data.local.PositionEntity
import cat.copernic.amayo.frontend.rutaManagment.data.local.RouteEntity

@Dao
interface RouteDao {
    @Insert
    suspend fun insertRoute(route: RouteEntity): Long
    @Insert
    suspend fun insertPosition(pos: PositionEntity)
    @Query("SELECT * FROM positions WHERE routeId = :routeId ORDER BY id")
    suspend fun getPositionsForRoute(routeId: Long): List<PositionEntity>
    @Query("DELETE FROM positions WHERE routeId = :routeId")
    suspend fun deletePositionsForRoute(routeId: Long)
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRoute(routeId: Long)
}
