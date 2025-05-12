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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRecompensas {

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
                                .dataCreacio(LocalDateTime.now())
                                .build()
                )
        );
    }

    @Test
    public void testGetAllOk() {
        String url = "http://localhost:" + port + "/api/recompensa/all";

        ResponseEntity<List<Recompensas>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recompensas>>() {
                }
        );

        List<Recompensas> receivedList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receivedList.size(), 1);
        System.out.println(receivedList);
    }

    @Test
    public void testGetByIdOk() {

        Recompensas carta = new Recompensas();

        carta.setDescripcio("Prueba2");
        carta.setObservacions("Prueba2");
        carta.setEstat(Estat.DISPONIBLES);
        carta.setCost(100);
        recompensasRepository.save(carta);

        String url = "http://localhost:" + port + "/api/recompensa/byId/" + carta.getId();

        ResponseEntity<Recompensas> response = restTemplate.exchange(
                url, HttpMethod.GET, null, Recompensas.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

}
