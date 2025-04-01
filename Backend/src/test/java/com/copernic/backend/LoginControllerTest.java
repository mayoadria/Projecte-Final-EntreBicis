package com.copernic.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSuccessfulLogin() throws Exception {
        // Se asume que el administrador existe (creado por SecurityConfig)
        // Y que la contraseña original es "admin"
        mockMvc.perform(post("/user")
                        .param("email", "admin@entrebicis.com")
                        .param("contra", "admin"))
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testUnsuccessfulLogin() throws Exception {
        // Intento de login con contraseña errónea
        mockMvc.perform(post("/user")
                        .param("email", "admin@entrebicis.com")
                        .param("contra", "claveIncorrecta"))
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
