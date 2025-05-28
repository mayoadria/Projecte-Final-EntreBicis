package cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote

import cat.copernic.amayo.frontend.recompensaManagment.model.Recompensa
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfície d'accés remot a l'API REST de Recompenses.
 *
 * Defineix les operacions CRUD per recuperar i actualitzar recompenses
 * mitjançant peticions HTTP asíncrones amb Retrofit.
 */
interface RecompensasApiRest {

    /**
     * Recupera totes les recompenses disponibles.
     *
     * @return Una resposta amb la llista de recompenses.
     */
    @GET("all")
    suspend fun findAll(): Response<List<Recompensa>>

    /**
     * Recupera una recompensa pel seu identificador.
     *
     * @param id Identificador de la recompensa a cercar.
     * @return Una resposta amb la recompensa trobada.
     */
    @GET("byId/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Recompensa>

    /**
     * Actualitza les dades d'una recompensa existent.
     *
     * @param product Recompensa amb les dades actualitzades.
     * @return Una resposta sense contingut (204 No Content si és correcte).
     */
    @PUT("update")
    suspend fun update(@Body product: Recompensa): Response<Void>

}