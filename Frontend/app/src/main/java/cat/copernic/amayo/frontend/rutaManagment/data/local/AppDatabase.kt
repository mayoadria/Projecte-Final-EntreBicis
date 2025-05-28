package cat.copernic.amayo.frontend.rutaManagment.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos de la aplicación utilizando Room, que contiene las entidades `RouteEntity` y `PositionEntity`.
 * Esta clase gestiona la base de datos y proporciona acceso a las entidades a través de los DAO (Data Access Objects).
 *
 * @constructor Constructor privado para garantizar que solo exista una instancia de la base de datos a través de `getInstance`.
 *
 * **Funciones**:
 * - `routeDao`: Obtiene el DAO para las rutas (`RouteDao`), que permite realizar operaciones CRUD sobre las entidades de rutas.
 *
 * **Uso**:
 * Esta clase se utiliza para interactuar con la base de datos local de la aplicación. La base de datos contiene dos entidades principales:
 * - `RouteEntity`: Representa la información sobre las rutas.
 * - `PositionEntity`: Representa la información sobre las posiciones asociadas a las rutas.
 *
 * El acceso a la base de datos se realiza mediante la instancia de `AppDatabase`, la cual se garantiza que sea única mediante el patrón Singleton.
 */
@Database(entities = [RouteEntity::class, PositionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Obtiene el DAO para las rutas.
     *
     * @return El objeto `RouteDao` que proporciona acceso a las operaciones CRUD de las rutas.
     */
    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        /**
         * Devuelve la instancia única de la base de datos, creando una nueva si no existe.
         *
         * @param ctx Contexto de la aplicación.
         * @return La instancia de la base de datos.
         */
        fun getInstance(ctx: Context) =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(ctx, AppDatabase::class.java, "app_db")
                    .build().also { INSTANCE = it }
            }
    }
}
