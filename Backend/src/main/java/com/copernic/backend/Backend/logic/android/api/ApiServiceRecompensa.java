package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

public interface ApiServiceRecompensa {
    final String BASE_URL = "/api/recompensa/";
    @GET(BASE_URL+"all")
    Call<List<Recompensas>> findAll();
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Recompensas usu);
    @GET(BASE_URL+"byId/{id}")
    Call<Recompensas> byId(@Path("id") Long id);

}
