package com.copernic.backend.Backend.logic.android;

import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

import java.util.List;

public interface ApiServiceUsuaris {
    final String BASE_URL = "/api/usuari/";

    @GET(BASE_URL+"read/all")
    Call<List<Usuari>> findAll();
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Usuari usu);
}
