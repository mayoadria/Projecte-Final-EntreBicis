package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiServiceUsuaris {
    final String BASE_URL = "/api/usuari/";

    @GET(BASE_URL+"read/all")
    Call<List<Usuari>> findAll();
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Usuari usu);
    @GET(BASE_URL+"getByEmail/{email}")
    Call<Usuari> byId(@Path("email") String prodId);

}
