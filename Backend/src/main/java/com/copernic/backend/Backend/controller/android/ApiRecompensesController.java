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

    /**
     * Logger per registrar missatges d'informació, advertències i errors del controlador.
     */
    private static final Logger logger = LoggerFactory.getLogger(ApiRecompensesController.class);

    /**
     * Component que conté la lògica de negoci per gestionar recompenses.
     */
    @Autowired
    private RecompensaLogic logic;

    /**
     * Inicialitza el controlador i mostra un missatge al log quan s'ha creat correctament.
     * Aquest mètode s'executa automàticament després de la construcció del bean.
     */
    @PostConstruct
    private void init() {
        logger.info("✅ ApiRecompensesController inicialitzat");
    }

    /**
     * Obté totes les recompenses per a l'aplicació Android.
     * <p>
     * Retorna una llista de recompenses dins d'una resposta HTTP amb codi 200 si tot va bé.
     * Si hi ha algun error, es retorna una resposta amb codi 500.
     * </p>
     *
     * @return ResponseEntity amb la llista de recompenses o un error si ha fallat.
     */
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

    /**
     * Obté una recompensa pel seu ID.
     * <p>
     * Si es troba, retorna la recompensa amb codi 200. Si no existeix, retorna codi 404.
     * En cas d'error, retorna codi 500.
     * </p>
     *
     * @param id Identificador de la recompensa a buscar.
     * @return ResponseEntity amb la recompensa trobada o un codi d'error si no s'ha trobat o hi ha hagut problemes.
     */
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

    /**
     * Actualitza una recompensa existent.
     * <p>
     * Si la recompensa no existeix o les dades són incorrectes, retorna codi 400 o 404 segons el cas.
     * Si l'actualització és correcta, retorna codi 200. En cas d'error, retorna codi 500.
     * </p>
     *
     * @param recompensa Objecte recompensa amb les dades actualitzades.
     * @return ResponseEntity buida amb el codi d'estat corresponent segons el resultat.
     */
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
