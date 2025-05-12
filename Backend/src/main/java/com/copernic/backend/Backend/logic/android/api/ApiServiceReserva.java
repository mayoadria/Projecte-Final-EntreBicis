package com.copernic.backend.Backend.logic.android.api;

import com.copernic.backend.Backend.entity.Reserva;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiServiceReserva {

    final String BASE_URL = "/api/reserva";
    @POST(BASE_URL + "/crear")
    Call<Long> save(@Body Reserva reserva);

    /* ---------- READ ---------- */
    @GET(BASE_URL + "/all")
    Call<List<Reserva>> findAll();

    @GET(BASE_URL + "/byId/{id}")
    Call<Reserva> findById(@Path("id") Long id);

    /* ---------- UPDATE ---------- */
    @PUT(BASE_URL + "/update")
    Call<Void> update(@Body Reserva reserva);

    /* ---------- DELETE ---------- */
    @DELETE(BASE_URL + "/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);
}
