package com.copernic.backend.Backend.logic.android;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RetroFitClient {
    public static Retrofit getRetrofitClient(String baseUrl) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Crea un Gson con el formato de fecha personalizado
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd") // Esto asegura que las fechas se manejen en el formato adecuado
                .create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
