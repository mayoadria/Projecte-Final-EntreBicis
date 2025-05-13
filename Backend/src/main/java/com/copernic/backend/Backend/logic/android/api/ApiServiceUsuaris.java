package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interfície per definir les operacions de consum d'API REST relacionades amb els usuaris.
 * Aquesta interfície és utilitzada per Retrofit per realitzar peticions HTTP al backend,
 * permetent consultar, actualitzar i buscar usuaris per email.
 */
public interface ApiServiceUsuaris {

    /**
     * Ruta base de l'API per accedir als endpoints d'usuaris.
     */
    String BASE_URL = "/api/usuari/";

    /**
     * Obté la llista de tots els usuaris registrats al sistema.
     *
     * @return Una crida {@link Call} que retorna una llista de {@link Usuari}.
     */
    @GET(BASE_URL + "read/all")
    Call<List<Usuari>> findAll();

    /**
     * Actualitza la informació d'un usuari existent.
     *
     * @param usu Objecte {@link Usuari} amb les dades actualitzades.
     * @return Una crida {@link Call} sense cos de resposta (Void) que indica èxit o error.
     */
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Usuari usu);

    /**
     * Obté la informació d'un usuari concret a partir del seu correu electrònic.
     *
     * @param prodId Correu electrònic de l'usuari a consultar.
     * @return Una crida {@link Call} que retorna l'objecte {@link Usuari} corresponent.
     */
    @GET(BASE_URL + "getByEmail/{email}")
    Call<Usuari> byId(@Path("email") String prodId);
}
