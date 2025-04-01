package com.copernic.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuariControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public void setUp() {
        // Limpiar la base de datos para los tests
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateUser() {
        String url = baseUrl + port + "/api/usuaris";
        Usuari user = new Usuari();
        user.setEmail("test@example.com");
        user.setNom("Test");
        user.setCognom("User");
        user.setContra("test1234");
        user.setTelefon("123456789");
        user.setPoblacio("TestCity");
        user.setRol(Rol.CICLISTA);  // Asignar rol para evitar problemas con getAuthorities()
        user.setSaldo(100);

        ResponseEntity<Usuari> response = restTemplate.postForEntity(url, user, Usuari.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @Order(2)
    public void testGetUser() {
        String url = baseUrl + port + "/api/usuaris/test@example.com";
        ResponseEntity<Usuari> response = restTemplate.getForEntity(url, Usuari.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test", response.getBody().getNom());
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        String url = baseUrl + port + "/api/usuaris/test@example.com";
        // Obtener el usuario actual
        ResponseEntity<Usuari> getResponse = restTemplate.getForEntity(url, Usuari.class);
        Usuari user = getResponse.getBody();
        user.setNom("UpdatedTest");
        // Realizar el update usando PUT
        restTemplate.put(url, user);
        // Volver a obtener el usuario para comprobar el cambio
        ResponseEntity<Usuari> updatedResponse = restTemplate.getForEntity(url, Usuari.class);
        assertEquals("UpdatedTest", updatedResponse.getBody().getNom());
    }

    @Test
    @Order(4)
    public void testDeleteUser() {
        String url = baseUrl + port + "/api/usuaris/test@example.com";
        restTemplate.delete(url);
        // Verificar que el usuario ya no existe
        ResponseEntity<Usuari> response = restTemplate.getForEntity(url, Usuari.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void testGetAllUsers() {
        // Creamos dos usuarios adicionales para probar el listado
        String url = baseUrl + port + "/api/usuaris";
        Usuari user1 = new Usuari();
        user1.setEmail("user1@example.com");
        user1.setNom("User1");
        user1.setCognom("Test");
        user1.setContra("clave123");
        user1.setTelefon("111111111");
        user1.setPoblacio("City1");
        user1.setRol(Rol.CICLISTA);
        user1.setSaldo(50);

        Usuari user2 = new Usuari();
        user2.setEmail("user2@example.com");
        user2.setNom("User2");
        user2.setCognom("Test");
        user2.setContra("clave456");
        user2.setTelefon("222222222");
        user2.setPoblacio("City2");
        user2.setRol(Rol.CICLISTA);
        user2.setSaldo(75);

        restTemplate.postForEntity(url, user1, Usuari.class);
        restTemplate.postForEntity(url, user2, Usuari.class);

        // Listar todos los usuarios
        ResponseEntity<List<Usuari>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Usuari>>() {});
        List<Usuari> users = response.getBody();
        // Se espera que queden en la BD user1 y user2 (el usuario "test@example.com" fue eliminado)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Dependiendo de la base de datos, el total debería ser 2
        assertEquals(2, users.size());
    }
}
