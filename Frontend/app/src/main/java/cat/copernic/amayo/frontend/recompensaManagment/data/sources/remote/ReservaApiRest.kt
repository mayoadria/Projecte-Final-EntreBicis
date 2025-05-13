package cat.copernic.mycards.mycards_frontend.user_management.data.sources.remote

import cat.copernic.amayo.frontend.recompensaManagment.model.Reserva
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfície d'accés a l'API REST per gestionar reserves.
 *
 * Defineix les operacions CRUD per crear, obtenir, actualitzar i eliminar reserves
 * utilitzant peticions HTTP asíncrones amb Retrofit.
 */
interface ReservaApiRest {

    /**
     * Desa una nova reserva a la base de dades.
     *
     * @param reserva Objecte reserva que es vol crear.
     * @return Resposta amb l'identificador de la reserva creada.
     */
    @POST("crear")
    suspend fun save(@Body reserva: Reserva): Response<Long>

    /**
     * Recupera totes les reserves existents.
     *
     * @return Resposta amb la llista de reserves.
     */
    @GET("all")
    suspend fun findAll(): Response<List<Reserva>>

    /**
     * Busca una reserva pel seu identificador.
     *
     * @param id Identificador de la reserva.
     * @return Resposta amb la reserva trobada.
     */
    @GET("byId/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Reserva>

    /**
     * Actualitza la informació d'una reserva existent.
     *
     * @param product Reserva amb les dades actualitzades.
     * @return Resposta sense contingut (204 No Content si és correcte).
     */
    @PUT("update")
    suspend fun update(@Body product: Reserva): Response<Void>

    /**
     * Elimina una reserva pel seu identificador.
     *
     * @param prodId Identificador de la reserva a eliminar.
     * @return Resposta sense contingut (204 No Content si és correcte).
     */
    @DELETE("delete/{resID}")
    suspend fun deleteById(@Path("resID") prodId: Long): Response<Void>
}
