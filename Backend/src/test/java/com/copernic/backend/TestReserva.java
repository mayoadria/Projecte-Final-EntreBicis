package com.copernic.backend;

import com.copernic.backend.Backend.controller.web.ReservaCaduca;
import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatReserva;
import com.copernic.backend.Backend.logic.android.RetrofitTLS;
import com.copernic.backend.Backend.logic.android.api.ApiServiceRecompensa;
import com.copernic.backend.Backend.logic.android.api.ApiServiceReserva;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import com.copernic.backend.Backend.repository.ReservaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestReserva {
    List<Reserva> reservas;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReservaRepository reservaRepository;
    private ApiServiceReserva apiService;
    @Autowired
    private ReservaCaduca reservaCaduca;

    @BeforeAll
    public void init() throws MalformedURLException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, Exception {
        apiService = RetrofitTLS
                .getRetrofitTLSClient("https://localhost:" + port)
                .create(ApiServiceReserva.class);
    }

    @BeforeEach
    public void setup() {
        reservaRepository.deleteAll();

        reservas = List.of(
                reservaRepository.save(
                        Reserva.builder()
                                .caducada(false)
                                .datareserva(null)
                                .estat(EstatReserva.CADUCADA)
                                .emailUsuari(null)
                                .idRecompensa(null)
                                .build()
                )
        );

    }

    @Test
    public void testGetAllOk() throws JsonProcessingException, IOException {

        Call<List<Reserva>> call = apiService.findAll();
        Response<List<Reserva>> response = call.execute();

        //if all is fine
        if (response.isSuccessful()) {
            //then get body info
            List<Reserva> receivedList = response.body();
            assertEquals(receivedList, reservas);
            assertEquals(receivedList.size(), 1);
        } else fail();
    }

    @Test
    public void testGetByIdOk() throws IOException {

        Reserva sample = reservas.get(0);
        Call<Reserva> call = apiService.findById(sample.getId());
        Response<Reserva> response = call.execute();

        assertEquals(HttpStatus.OK.value(), response.code());
        assertEquals(sample, response.body());
    }

    @Test
    public void testGetByIdNotFound() throws IOException {

        Call<Reserva> call = apiService.findById(999_999L);
        Response<Reserva> response = call.execute();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
        assertNull(response.body());
    }

    @Test
    public void testCreateOk() throws IOException {

        Reserva nueva = Reserva.builder()
                .caducada(false)
                .datareserva(null)
                .estat(EstatReserva.RESERVADA)
                .emailUsuari(null)
                .idRecompensa(null)
                .build();

        Call<Long> call = apiService.save(nueva);
        Response<Long> response = call.execute();

        assertEquals(HttpStatus.CREATED.value(), response.code());
        Long idCreada = response.body();
        assertNotNull(idCreada);

        /* verificación en BD */
        assertTrue(reservaRepository.existsById(idCreada));
    }



    @Test
    public void testUpdateOk() throws IOException {

        Reserva original = reservas.get(0);

        Reserva modificada = Reserva.builder()
                .id(original.getId())
                .caducada(true)
                .datareserva(original.getDatareserva())
                .estat(EstatReserva.CADUCADA)
                .emailUsuari(original.getEmailUsuari())
                .idRecompensa(original.getIdRecompensa())
                .build();

        Call<Void> call = apiService.update(modificada);
        Response<Void> response = call.execute();

        assertEquals(HttpStatus.OK.value(), response.code());

        Reserva fromDb = reservaRepository.findById(original.getId()).orElse(null);
        assertNotNull(fromDb);
        assertEquals(modificada.getCaducada(), fromDb.getCaducada());
        assertEquals(modificada.getEstat(), fromDb.getEstat());
    }

    @Test
    public void testUpdateNotFound() throws IOException {

        Reserva fantasma = Reserva.builder()
                .id(123456L)
                .caducada(true)
                .estat(EstatReserva.CADUCADA)
                .build();

        Call<Void> call = apiService.update(fantasma);
        Response<Void> response = call.execute();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    }

    @Test
    public void testDeleteOk() throws IOException {

        Reserva sample = reservas.get(0);

        Call<Void> call = apiService.deleteById(sample.getId());
        Response<Void> response = call.execute();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
        assertFalse(reservaRepository.existsById(sample.getId()));
    }

    @Test
    public void testDeleteNotFound() throws IOException {

        Call<Void> call = apiService.deleteById(888888L);
        Response<Void> response = call.execute();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    }

    @Test
    public void testReservaCaducaJob() {

        // Creamos reserva activa con fecha pasada
        Reserva caducable = reservaRepository.save(
                Reserva.builder()
                        .caducada(true)
                        .estat(EstatReserva.CADUCADA)
                        .datareserva(LocalDateTime.now().minusMonths(2))
                        .build());

        // Simulamos la tarea programada
        reservaCaduca.revisarReservesCaducades();

        Reserva actualizada = reservaRepository.findById(caducable.getId()).orElseThrow();
        assertTrue(actualizada.getCaducada());
        assertEquals(EstatReserva.CADUCADA, actualizada.getEstat());
    }
}
