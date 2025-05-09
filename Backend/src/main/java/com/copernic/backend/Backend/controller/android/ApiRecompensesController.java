package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.logic.web.RecompensaLogic;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensa")
public class ApiRecompensesController {

    private static final Logger logger = LoggerFactory.getLogger(ApiRecompensesController.class);

    @Autowired
    private RecompensaLogic logic;

    @PostConstruct
    private void init() {
        logger.info("✅ ApiRecompensesController inicialitzat");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recompensas>> findAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {
            List<Recompensas> recompensas = logic.llistarRecompensasAndroid();
            logger.info("🔍 {} recompenses recuperades", recompensas.size());
            return new ResponseEntity<>(recompensas, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al recuperar totes les recompenses", e);
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<Recompensas> findByID(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {
            Recompensas recompensa = logic.findById(id);
            if (recompensa == null) {
                logger.error("⚠️ Recompensa amb ID {} no trobada", id);
                return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            } else {
                logger.info("📦 Recompensa trobada amb ID {}", id);
                return new ResponseEntity<>(recompensa, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("❌ Error al recuperar recompensa amb ID {}", id, e);
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateRecompensa(@RequestBody Recompensas recompensa) {
        try {
            if (recompensa == null || recompensa.getId() == null) {
                logger.error("❌ Recompensa buida o sense ID a l'actualització");
                return ResponseEntity.badRequest().build();
            }

            if (!logic.existeRecompensa(recompensa.getId())) {
                logger.error("⚠️ No existeix la recompensa amb ID {}", recompensa.getId());
                return ResponseEntity.notFound().build();
            }

            logic.guardarRecompensa(recompensa);
            logger.info("✅ Recompensa actualitzada amb ID {}", recompensa.getId());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("❌ Error actualitzant recompensa amb ID {}",
                    recompensa != null ? recompensa.getId() : "null", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
