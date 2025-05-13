package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

/**
 * Interfície per definir les operacions de consum d'API REST relacionades amb recompenses.
 * Aquesta interfície és utilitzada per Retrofit per realitzar peticions HTTP al backend.
 * Inclou operacions per llistar totes les recompenses, actualitzar-ne una i buscar per ID.
 */
public interface ApiServiceRecompensa {

    /**
     * Ruta base de l'API per accedir als endpoints de recompenses.
     */
    String BASE_URL = "/api/recompensa/";

    /**
     * Obté totes les recompenses registrades al sistema.
     *
     * @return Una crida {@link Call} que retorna una llista de {@link Recompensas}.
     */
    @GET(BASE_URL + "all")
    Call<List<Recompensas>> findAll();

    /**
     * Actualitza la informació d'una recompensa existent.
     *
     * @param usu Objecte {@link Recompensas} amb les dades actualitzades.
     * @return Una crida {@link Call} sense cos de resposta (Void) que indica èxit o error.
     */
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Recompensas usu);

    /**
     * Obté una recompensa específica a partir del seu identificador.
     *
     * @param id Identificador únic de la recompensa a consultar.
     * @return Una crida {@link Call} que retorna l'objecte {@link Recompensas} corresponent.
     */
    @GET(BASE_URL + "byId/{id}")
    Call<Recompensas> byId(@Path("id") Long id);
}
