package com.copernic.backend.Backend.logic.web;

import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.repository.SistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SistemaLogic {

    @Autowired
    private SistemaRepository sistemaRepository;

    // Método para obtener el sistema
    public Sistema getSistema() {
        return sistemaRepository.findById(1L)
                .orElse(new Sistema(1L, 0.0, Duration.ZERO, 0, Duration.ZERO));
    }

    // Método para guardar un sistema
    public void guardarSistema(Sistema sistema) {
        sistemaRepository.save(sistema);
    }

    // Método para obtener el tiempo de recogida
    public Duration getTempsRecollida() {
        return getSistema().getTempsRecollida();
    }

    // Puedes agregar más métodos para manejar la lógica de caducidad de la reserva aquí
}
