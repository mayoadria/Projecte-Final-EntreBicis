package com.copernic.backend;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.android.api.ApiServiceUsuaris;
import com.copernic.backend.Backend.logic.android.RetrofitTLSClient;
import com.copernic.backend.Backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import retrofit2.Call;
import retrofit2.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
public class TestUsuariAndroid {

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
        apiService = RetrofitTLSClient
                .getRetrofitTLSClient("https://localhost:" + port)
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
    public void testGetByIdOk() throws JsonProcessingException, IOException {

        Usuari usuari = Usuari.builder()
                .email("usuari@test.com")
                .nom("Usuari Test")
                .cognom("Cognom Test")
                .telefon("666666666")
                .poblacio("TestPoblacio")
                .contra(passwordEncoder.encode("contrasenya"))
                .saldo(100.0)
                .foto(null)
                .rol(Rol.CICLISTA)
                .estat(EstatUsuari.ACTIU)
                .rutes(Collections.emptyList())
                .recompensas(Collections.emptyList())
                .build();

        userRepository.save(usuari);

        Call<Usuari> call = apiService.byId(usuari.getEmail());
        Response<Usuari> response = call.execute();

        if (response.isSuccessful())
        {
            Usuari usuarip2 = response.body();
            assertEquals(HttpStatus.OK.value(),response.code());
            assertEquals(usuari, usuarip2);
        }
        else fail();
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


    @Test
    public void testGetAllEmpty() throws IOException {
        // No hay usuarios en la BBDD
        userRepository.deleteAll();

        Call<List<Usuari>> call = apiService.findAll();
        Response<List<Usuari>> response = call.execute();

        // Debería responder OK pero con lista vacía
        assertTrue(response.isSuccessful());
        List<Usuari> receivedList = response.body();
        assertNotNull(receivedList);
        assertTrue(receivedList.isEmpty());
    }

    @Test
    public void testGetByIdNotFound() throws IOException {
        // Email que no existe
        String missingEmail = "noexiste@test.com";

        Call<Usuari> call = apiService.byId(URLEncoder.encode(missingEmail, StandardCharsets.UTF_8));
        Response<Usuari> response = call.execute();

        // Ahora esperamos 400 en lugar de 404
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertNull(response.body());
    }



    @Test
    public void testUpdateBadEmailFormat() throws IOException {
        // Preparamos un usuario válido
        Usuari u = Usuari.builder()
                .email("valido@test.com")
                .nom("Valido")
                .cognom("Usuario")
                .telefon("611611611")
                .poblacio("Ciudad")
                .contra(passwordEncoder.encode("pwd"))
                .saldo(0.0)
                .rol(Rol.ADMINISTRADOR)
                .estat(EstatUsuari.ACTIU)
                .rutes(Collections.emptyList())
                .recompensas(Collections.emptyList())
                .build();
        userRepository.save(u);

        // Intentamos actualizar con email inválido (y además no existente)
        Usuari update = new Usuari();
        update.setEmail("malformato.com");
        update.setNom("Nuevo");
        update.setCognom("Nombre");
        update.setTelefon("622622622");
        update.setPoblacio("OtraCiudad");
        update.setContra(passwordEncoder.encode("pwd"));
        update.setRol(Rol.ADMINISTRADOR);

        Call<Void> call = apiService.update(update);
        Response<Void> response = call.execute();

        // Ahora esperamos 404 en lugar de 400
        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    }


    @Test
    public void testUpdateNotFound() throws IOException {
        // Intentar actualizar un usuario que no existe
        Usuari update = Usuari.builder()
                .email("noexiste@test.com")
                .nom("No")
                .cognom("Existe")
                .telefon("633633633")
                .poblacio("CiudadX")
                .contra(passwordEncoder.encode("pwd"))
                .rol(Rol.CICLISTA)
                .build();

        Call<Void> call = apiService.update(update);
        Response<Void> response = call.execute();

        // Debería 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND.value(), response.code());
    }


}

