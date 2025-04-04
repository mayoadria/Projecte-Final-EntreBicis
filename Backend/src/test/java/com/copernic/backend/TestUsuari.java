package com.copernic.backend;

import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.Estat;
import com.copernic.backend.Backend.entity.enums.EstatUsuari;
import com.copernic.backend.Backend.entity.enums.Rol;
import com.copernic.backend.Backend.logic.android.UsuariAndroidLogic;
import com.copernic.backend.Backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TestUsuari {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsuariAndroidLogic usuariAndroidLogic;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testUpdateUsuari_success() throws Exception {
        // Preparación: crear y guardar un usuario existente
        Usuari original = Usuari.builder()
                .email("test@example.com")
                .nom("John")
                .cognom("Doe")
                .contra("pass1234")
                .telefon("123456789")
                .poblacio("City")
                .saldo(100.0)
                .foto("fotoBase64")
                .rol(Rol.CICLISTA)       // Asegúrate de que estos enums existen en tu proyecto
                .estat(EstatUsuari.ACTIU)
                .build();
        userRepository.save(original);

        // Crear objeto con la información actualizada
        Usuari updateInfo = Usuari.builder()
                .nom("Jane")
                .cognom("Smith")
                .contra("newpass1")   // Nueva contraseña
                .telefon("987654321")
                .poblacio("NewCity")
                .saldo(200.0)
                .foto("newFotoBase64")
                .build();

        // Llamar al metodo de actualización
        Usuari updatedUser = usuariAndroidLogic.updateUsuari("test@example.com", updateInfo);

        // Verificaciones
        assertEquals("Jane", updatedUser.getNom());
        assertEquals("Smith", updatedUser.getCognom());
        // Verificamos que la contraseña se haya actualizado y encriptado
        assertTrue(passwordEncoder.matches("newpass1", updatedUser.getContra()));
        assertEquals("987654321", updatedUser.getTelefon());
        assertEquals("NewCity", updatedUser.getPoblacio());
        assertEquals(200, updatedUser.getSaldo());
        assertEquals("newFotoBase64", updatedUser.getFoto());
    }

    @Test
    public void testUpdateUsuari_notFound() {
        // Crear datos de actualización para un usuario que no existe
        Usuari updateInfo = Usuari.builder()
                .nom("Jane")
                .cognom("Smith")
                .contra("newpass1")
                .telefon("987654321")
                .poblacio("NewCity")
                .saldo(200.0)
                .foto("newFotoBase64")
                .build();

        // Se espera que al no encontrar el usuario se lance una excepción con el mensaje "Usuari no trobat"
        Exception exception = assertThrows(Exception.class, () -> {
            usuariAndroidLogic.updateUsuari("nonexistent@example.com", updateInfo);
        });
        assertEquals("Usuari no trobat", exception.getMessage());
    }
}
