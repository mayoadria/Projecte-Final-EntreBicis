package com.copernic.backend;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import com.copernic.backend.Backend.repository.RecompensasRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRecompensas {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecompensasRepository recompensasRepository;
    @BeforeAll
    public void setUp() {
        recompensasRepository.deleteAll();

        Recompensas carta = new Recompensas();

        carta.setDescripcio("Prueba");
        carta.setObservacions("Prueba");
        carta.setEstat(Estat.ACTIU);
        carta.setCost(100);

        recompensasRepository.save(carta);
    }

    @Test
    public void testGetAllOk(){
        String url = "http://localhost:" + port + "/api/recompensa/all";

        ResponseEntity<List<Recompensas>> response = restTemplate.exchange(
                url, HttpMethod.GET,null,new ParameterizedTypeReference<List<Recompensas>>(){}
        );

        List<Recompensas> receivedList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receivedList.size(), 1);
        System.out.println(receivedList);
    }

    @Test
    public void testGetByIdOk(){

        Recompensas carta = new Recompensas();

        carta.setDescripcio("Prueba2");
        carta.setObservacions("Prueba2");
        carta.setEstat(Estat.ACTIU);
        carta.setCost(100);
        recompensasRepository.save(carta);

        String url = "http://localhost:" + port + "/api/recompensa/byId/" + carta.getId();

        ResponseEntity<Recompensas> response = restTemplate.exchange(
                url, HttpMethod.GET,null,Recompensas.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

}
