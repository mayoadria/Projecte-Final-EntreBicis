package com.copernic.backend;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.android.RetrofitTLS;
import com.copernic.backend.Backend.logic.android.api.ApiServiceRecompensa;
import com.copernic.backend.Backend.logic.android.api.ApiServiceUsuaris;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import com.copernic.backend.Backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRecompensas {

    LocalDateTime fixedNow = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    List<Recompensas> recompensas;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecompensasRepository recompensasRepository;
    private ApiServiceRecompensa apiService;

    @BeforeAll
    public void init() throws MalformedURLException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, Exception {
        apiService = RetrofitTLS
                .getRetrofitTLSClient("https://localhost:" + port)
                .create(ApiServiceRecompensa.class);
    }

    @BeforeEach
    public void setup() {
        recompensasRepository.deleteAll();



        recompensas = List.of(
                recompensasRepository.save(
                        Recompensas.builder()
                                .descripcio("Prueba")
                                .observacions("Observaciones")
                                .cost(1)
                                .estat(Estat.DISPONIBLES)
                                .dataAsignacio(fixedNow)
                                .dataCreacio(fixedNow)
                                .dataReserva(fixedNow)
                                .dataEntrega(fixedNow)
                                .reservas(Collections.emptyList())
                                .usuariRecompensa(null)
                                .puntBescanviId(null)
                                .build()
                )
        );

    }

    @Test
    public void testGetAllOk() throws JsonProcessingException, IOException {

        Call<List<Recompensas>> call = apiService.findAll();
        Response<List<Recompensas>> response = call.execute();

        //if all is fine
        if (response.isSuccessful()) {
            //then get body info
            List<Recompensas> receivedList = response.body();
            assertEquals(receivedList, recompensas);
            assertEquals(receivedList.size(), 1);
        } else fail();
    }

    @Test
    public void testGetByIdOk() throws JsonProcessingException, IOException {

        Recompensas recompensa = Recompensas.builder()
                        .descripcio("Prueba2")
                        .observacions("Observaciones2")
                        .cost(1)
                        .estat(Estat.DISPONIBLES)
                        .dataAsignacio(null)
                        .dataCreacio(null)
                        .dataReserva(null)
                        .dataEntrega(null)
                        .reservas(Collections.emptyList())
                        .usuariRecompensa(null)
                        .puntBescanviId(null)
                        .build();

        recompensasRepository.save(recompensa);
        Call<Recompensas> call = apiService.byId(recompensa.getId());
        Response<Recompensas> response = call.execute();

        if (response.isSuccessful())
        {
            Recompensas usuarip2 = response.body();
            assertEquals(HttpStatus.OK.value(),response.code());
            assertEquals(recompensa, usuarip2);
        }
        else fail();
    }

    @Test
    public void testUpdateRecompensaOk() throws IOException {

        /* ---------- 1. Crear y guardar la recompensa inicial ---------- */
        Recompensas original = Recompensas.builder()
                .descripcio("Recompensa inicial")
                .observacions("Observación inicial")
                .cost(10)
                .estat(Estat.DISPONIBLES)
                .dataCreacio(fixedNow)
                .dataAsignacio(fixedNow)
                .dataReserva(fixedNow)
                .dataEntrega(fixedNow)
                .reservas(Collections.emptyList())
                .usuariRecompensa(null)
                .puntBescanviId(null)
                .build();

        original = recompensasRepository.save(original);   // ya tenemos ID

        /* ---------- 2. Objeto con los nuevos valores (mismo id) ---------- */
        Recompensas modificada = Recompensas.builder()
                .id(original.getId())                      // ¡clave!
                .descripcio("Recompensa modificada")
                .observacions("Observación modificada")
                .cost(20)
                .estat(Estat.RESERVADES)                    // pongo otro estado para ver el cambio
                // fechas: conservamos las existentes (o cámbialas si procede)
                .dataCreacio(original.getDataCreacio())
                .dataAsignacio(original.getDataAsignacio())
                .dataReserva(original.getDataReserva())
                .dataEntrega(original.getDataEntrega())
                .reservas(Collections.emptyList())
                .usuariRecompensa(null)
                .puntBescanviId(null)
                .build();

        /* ---------- 3. Llamada al endpoint PUT /update ---------- */
        Call<Void> call = apiService.update(modificada);
        Response<Void> response = call.execute();

        /* ---------- 4. Verificación de la respuesta HTTP ---------- */
        assertEquals(HttpStatus.OK.value(), response.code());

        /* ---------- 5. Verificación en la base de datos ---------- */
        Recompensas fromDb = recompensasRepository.findById(original.getId()).orElse(null);
        assertNotNull(fromDb);

        // Comprobamos que los campos realmente cambiaron
        assertEquals(modificada.getDescripcio(),   fromDb.getDescripcio());
        assertEquals(modificada.getObservacions(), fromDb.getObservacions());
        assertEquals(modificada.getCost(),         fromDb.getCost());
        assertEquals(modificada.getEstat(),        fromDb.getEstat());
    }


    @Test
    public void testUpdateNotFound() throws IOException {

        Recompensas modificada = Recompensas.builder()
                .id(123456L)                       // id fantasma
                .descripcio("Nada que actualizar")
                .observacions("No existe")
                .cost(99)
                .estat(Estat.DISPONIBLES)
                .dataCreacio(fixedNow)
                .build();

        Call<Void> call = apiService.update(modificada);
        Response<Void> response = call.execute();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    }

}
