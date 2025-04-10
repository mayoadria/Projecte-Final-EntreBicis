package com.copernic.backend;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.android.ApiServiceUsuaris;
import com.copernic.backend.Backend.logic.android.RetroFitClient;
import com.copernic.backend.Backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import retrofit2.Call;
import retrofit2.Response;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestUsuari {

    List<Usuari> usuaris;
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private ApiServiceUsuaris apiService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public void init() throws MalformedURLException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, Exception {
        apiService = RetroFitClient
                .getRetrofitClient("http://localhost:" + port)
                .create(ApiServiceUsuaris.class);
    }

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        usuaris = List.of(

                userRepository.save(Usuari.builder()
                        .email("pruebagmail.com")
                        .nom("Dragón ")
                        .cognom("prueba")
                        .telefon("123456789")
                        .poblacio("prueba")
                        .contra(passwordEncoder.encode("prueba"))
                        .saldo(0.0) // Valor por defecto
                        .foto(null) // Valor por defecto
                        .rol(Rol.ADMINISTRADOR)
                        .estat(EstatUsuari.ACTIU) // Ajusta según el valor por defecto o el necesario
                        .rutes(Collections.emptyList()) // Inicializa la lista como vacía
                        .recompensas(Collections.emptyList()) // Inicializa la lista como vacía
                        .build()));

    }

    @Test
    public void testGetAllOk() throws JsonProcessingException, IOException {

        Call<List<Usuari>> call = apiService.findAll();
        Response<List<Usuari>> response = call.execute();

        //if all is fine
        if (response.isSuccessful()) {
            //then get body info
            List<Usuari> receivedList = response.body();
            assertEquals(receivedList, usuaris);
            assertEquals(receivedList.size(), 1);
        } else fail();
    }

    @Test
    public void testUpdateOk() throws IOException {

        Usuari carta = new Usuari();
        carta.setNom("prueba");
        carta.setCognom("prueba");
        carta.setNom("Dragón a modificar"); // Esto sobrescribe el nombre anterior
        carta.setTelefon("123456789");
        carta.setPoblacio("prueba");
        carta.setContra(passwordEncoder.encode("prueba"));
        carta.setEmail("prueba@gmail.com"); // Posible error de formato en el email
        carta.setRol(Rol.ADMINISTRADOR);
        userRepository.save(carta);

        Usuari carta2 = new Usuari();

        carta2.setNom("pepe");
        carta2.setCognom("pepe");
        carta2.setNom("pepe a pepe"); // Esto sobrescribe el nombre anterior
        carta2.setTelefon("pepe");
        carta2.setPoblacio("prueba");
        carta2.setContra(passwordEncoder.encode("prueba"));
        carta2.setRol(Rol.ADMINISTRADOR);
        carta2.setEmail(carta.getEmail());

        Call<Void> call = apiService.update(carta2);
        Response<Void> response = call.execute();

        //verify response
        assertEquals(HttpStatus.OK.value(), response.code());

        //db verify
        Usuari modifiedProduct = userRepository.findById(carta.getEmail()).orElse(null);
        assertTrue(carta2.equals(modifiedProduct));
    }
}
