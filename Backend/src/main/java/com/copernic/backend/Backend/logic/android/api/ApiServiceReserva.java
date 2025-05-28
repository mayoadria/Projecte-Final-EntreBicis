package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Reserva;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interfície per definir les operacions de consum d'API REST relacionades amb les reserves.
 * Aquesta interfície s'utilitza amb Retrofit per fer peticions HTTP al backend,
 * permetent crear, consultar, actualitzar i eliminar reserves.
 */
public interface ApiServiceReserva {

    /**
     * Ruta base de l'API per accedir als endpoints de reserves.
     */
    String BASE_URL = "/api/reserva";

    /**
     * Crea una nova reserva al sistema.
     *
     * @param reserva Objecte {@link Reserva} amb les dades de la reserva a crear.
     * @return Una crida {@link Call} que retorna l'ID de la reserva creada.
     */
    @POST(BASE_URL + "/crear")
    Call<Long> save(@Body Reserva reserva);

    /**
     * Obté totes les reserves registrades al sistema.
     *
     * @return Una crida {@link Call} que retorna una llista de {@link Reserva}.
     */
    @GET(BASE_URL + "/all")
    Call<List<Reserva>> findAll();

    /**
     * Obté una reserva específica a partir del seu identificador.
     *
     * @param id Identificador únic de la reserva a consultar.
     * @return Una crida {@link Call} que retorna l'objecte {@link Reserva} corresponent.
     */
    @GET(BASE_URL + "/byId/{id}")
    Call<Reserva> findById(@Path("id") Long id);

    /**
     * Actualitza la informació d'una reserva existent.
     *
     * @param reserva Objecte {@link Reserva} amb les dades actualitzades.
     * @return Una crida {@link Call} sense cos de resposta (Void) que indica èxit o error.
     */
    @PUT(BASE_URL + "/update")
    Call<Void> update(@Body Reserva reserva);

    /**
     * Elimina una reserva a partir del seu identificador.
     *
     * @param id Identificador únic de la reserva a eliminar.
     * @return Una crida {@link Call} sense cos de resposta (Void) que indica èxit o error.
     */
    @DELETE(BASE_URL + "/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);
}
