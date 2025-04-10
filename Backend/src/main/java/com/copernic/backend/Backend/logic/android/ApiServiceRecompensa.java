package com.copernic.backend.Backend.logic.android;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface ApiServiceRecompensa {
    final String BASE_URL = "/api/recompensa/";
    @PUT(BASE_URL + "update")
    Call<Void> update(@Body Recompensas usu);

}
