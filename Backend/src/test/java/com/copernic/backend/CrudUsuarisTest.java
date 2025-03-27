package com.copernic.backend;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CrudUsuarisTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Autowired
    private UserRepository userRepository;

    // Metodo auxiliar para crear un Usuari de prueba
    private Usuari getTestUsuari() {
        return Usuari.builder()
                .email("test@example.com")
                .nom("Test")
                .cognom("User")
                .contra("password")
                .telefon("123456789")
                .poblacio("TestCity")
                .saldo(100)
                .rol(Rol.CICLISTA)  // Asumiendo que tienes un valor Rol.USER en tu enum
                .build();
    }

    @Test
    public void testCreateReadUpdateDeleteUsuari() throws Exception {
        Usuari usuari = getTestUsuari();

        // --- Crear ---
        MvcResult createResult = mockMvc.perform(post("/api/usuaris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuari)))
                .andExpect(status().isCreated())
                .andReturn();

        Usuari createdUsuari = objectMapper.readValue(createResult.getResponse().getContentAsString(), Usuari.class);
        Assertions.assertEquals(usuari.getEmail(), createdUsuari.getEmail());

        // --- Leer (obtener por email) ---
        MvcResult getResult = mockMvc.perform(get("/api/usuaris/" + usuari.getEmail()))
                .andExpect(status().isOk())
                .andReturn();

        Usuari readUsuari = objectMapper.readValue(getResult.getResponse().getContentAsString(), Usuari.class);
        Assertions.assertEquals(usuari.getNom(), readUsuari.getNom());

        // --- Actualizar ---
        createdUsuari.setNom("UpdatedName");

        MvcResult updateResult = mockMvc.perform(put("/api/usuaris/" + usuari.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUsuari)))
                .andExpect(status().isOk())
                .andReturn();

        Usuari updatedUsuari = objectMapper.readValue(updateResult.getResponse().getContentAsString(), Usuari.class);
        Assertions.assertEquals("UpdatedName", updatedUsuari.getNom());

        // --- Eliminar ---
        mockMvc.perform(delete("/api/usuaris/" + usuari.getEmail()))
                .andExpect(status().isOk());

        // Verificar que la eliminación es correcta
        mockMvc.perform(get("/api/usuaris/" + usuari.getEmail()))
                .andExpect(status().isNotFound());
    }
}
