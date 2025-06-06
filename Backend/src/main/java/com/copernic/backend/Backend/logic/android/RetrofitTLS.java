package com.copernic.backend.Backend.logic.android;

import com.copernic.backend.Backend.logic.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Classe utilitària per crear una instància de {@link Retrofit} amb connexió TLS personalitzada.
 * Aquesta configuració permet acceptar tots els certificats SSL (desenvolupament/testing),
 * incloent-hi certificats autofirmats. També registra adaptadors per tractar tipus {@code LocalDateTime}.
 */
public class RetrofitTLS {

    /**
     * Crea i retorna una instància de {@link Retrofit} configurada amb TLS que accepta tots els certificats.
     * Utilitza un {@link TrustManager} permissiu per evitar errors de certificats no vàlids en desenvolupament.
     * També configura Gson amb un {@link LocalDateTimeAdapter} per la correcta serialització/deserialització de dates.
     *
     * @param baseUrl URL base del servei REST (ex: https://api.exemple.com/).
     * @return Instància configurada de {@link Retrofit} amb suport TLS permissiu.
     * @throws RuntimeException Si hi ha un error configurant TLS.
     */
    public static Retrofit getRetrofitTLSClient(String baseUrl) {

        try {
            // TrustManager que acepta todos los certificados (solo para desarrollo)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            // ✅ Aquí registras los adapters para fechas:
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error configurando TLS en Retrofit", e);
        }
    }
}

