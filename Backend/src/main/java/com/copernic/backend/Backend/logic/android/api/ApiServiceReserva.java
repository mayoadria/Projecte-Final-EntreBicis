package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Reserva;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServiceReserva {

    final String BASE_URL = "/api/reserva/";

    @POST(BASE_URL+"crear")
    Call<Long> save(@Body Reserva prod);
}
