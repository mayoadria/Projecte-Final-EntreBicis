package com.copernic.backend.Backend.logic.android;

import com.copernic.backend.Backend.logic.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe utilitària per gestionar la instància singleton de {@link Retrofit}.
 * Configura un client Retrofit amb la base URL especificada i un convertidor Gson personalitzat
 * per tractar objectes {@code LocalDateTime}.
 */
public class RetroFitClient {

    /**
     * Instància singleton de Retrofit per evitar múltiples inicialitzacions.
     */
    private static Retrofit retrofit = null;

    /**
     * Obté una instància de {@link Retrofit} configurada amb la base URL proporcionada.
     * Registra un adaptador personalitzat per a la serialització/deserialització de {@code LocalDateTime}.
     * La instància es crea una única vegada (patró Singleton) i es reutilitza per les següents crides.
     *
     * @param baseUrl URL base del servei REST (ex: https://api.exemple.com/).
     * @return Instància configurada de {@link Retrofit}.
     */
    public static Retrofit getRetrofitClient(String baseUrl) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}

