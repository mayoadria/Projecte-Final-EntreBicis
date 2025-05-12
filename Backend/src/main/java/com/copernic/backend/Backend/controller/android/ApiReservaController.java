package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
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
@RequestMapping("/api/reserva")
public class ApiReservaController {
    /**
     * Logger per registrar missatges d'informació, advertències i errors del controlador de reserves.
     */
    private static final Logger logger = LoggerFactory.getLogger(ApiReservaController.class);
    /**
     * Component que gestiona la lògica de negoci relacionada amb les reserves.
     */
    @Autowired
    private ReservaLogic reservaLogic;

    /**
     * Inicialitza el controlador després de crear-se el bean.
     * Actualment no fa cap acció, però es pot utilitzar per inicialitzacions futures.
     */
    @PostConstruct
    private void init() {
    }

    /**
     * Crea una nova reserva.
     * <p>
     * Si la reserva és vàlida, es guarda i es retorna el seu ID amb codi 201. Si la reserva és null, es retorna codi 404.
     * En cas d'error durant el procés, es retorna codi 500.
     * </p>
     *
     * @param reserva Objecte reserva que es vol crear.
     * @return ResponseEntity amb l'ID de la nova reserva o un codi d'error si ha fallat.
     */
    @PostMapping("/crear")
    public ResponseEntity<Long> save(@RequestBody Reserva reserva) {

        ResponseEntity response;
        Long prodId;

        try {
            if (reserva == null) {
                logger.error("❌ Intent de creació amb reserva null");
                response = ResponseEntity.notFound().build();
            } else {

                prodId = reservaLogic.saveReserva(reserva);
                response = new ResponseEntity<>(prodId, HttpStatus.CREATED);

            }

        } catch (Exception e) {
            logger.error("❌ Error al crear reserva", e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Busca una reserva pel seu ID.
     * <p>
     * Si es troba, retorna la reserva amb codi 200. Si no existeix, retorna codi 404.
     * En cas d'error durant la consulta, es retorna codi 500.
     * </p>
     *
     * @param id Identificador de la reserva a cercar.
     * @return ResponseEntity amb la reserva trobada o un codi d'error si no s'ha trobat o hi ha hagut problemes.
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<Reserva> findByID(@PathVariable Long id) {
        Reserva reserva;
        ResponseEntity<Reserva> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", " no-store");

        try {
            reserva = reservaLogic.findById(id);
            if (reserva == null) {
                logger.error("⚠️ Reserva no trobada amb ID: {}", id);
                response = new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            } else {
                logger.info("📦 Reserva trobada amb ID: {}", id);
                response = new ResponseEntity<>(reserva, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("❌ Error al buscar reserva amb ID: {}", id, e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
    /**
     * Recupera totes les reserves existents.
     * <p>
     * Retorna una llista de reserves amb codi 200 si la consulta té èxit.
     * En cas d'error, retorna una resposta amb codi 500.
     * </p>
     *
     * @return ResponseEntity amb la llista de reserves o un codi d'error si ha fallat.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Reserva>> findAll() {

        List<Reserva> reservas;

        ResponseEntity<List<Reserva>> response;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", " no-store");

        try {
            reservas = reservaLogic.llistarReserva();
            logger.info("🔍 {} reserves trobades", reservas.size());
            response = new ResponseEntity<>(reservas, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error al recuperar totes les reserves", e);
            response = new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    /**
     * Elimina una reserva pel seu ID.
     * <p>
     * Si la reserva existeix, s'elimina i es retorna codi 204 (No Content).
     * Si no existeix, es retorna codi 404. Si l'ID és null, es retorna codi 400.
     * En cas d'error, es retorna codi 500.
     * </p>
     *
     * @param resID Identificador de la reserva a eliminar.
     * @return ResponseEntity buida amb el codi d'estat corresponent segons el resultat.
     */
    @DeleteMapping("/delete/{resID}")
    public ResponseEntity<Void> deleteById(@PathVariable Long resID){

        ResponseEntity<Void> response;

        try {
            if (resID != null)
            {
                if (reservaLogic.existsById(resID))
                {
                    reservaLogic.deleteReservaById(resID);
                    response = ResponseEntity.noContent().build();
                }
                else
                {
                    response = ResponseEntity.notFound().build();
                }
            }else
            {
                response = ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            logger.error("❌ Error eliminant reserva amb ID: {}", resID, e);
            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }

    /**
     * Actualitza una reserva existent.
     * <p>
     * Si la reserva existeix, s'actualitza i es retorna codi 200. Si no existeix, es retorna codi 404.
     * Si la reserva és null, es retorna codi 400. En cas d'error, es retorna codi 500.
     * </p>
     *
     * @param reserva Objecte reserva amb les dades actualitzades.
     * @return ResponseEntity buida amb el codi d'estat corresponent segons el resultat.
     */
    @PutMapping("/update")
    public ResponseEntity<Void> updateReserva(@RequestBody Reserva reserva) {

        ResponseEntity<Void> resposta;

        try {
            if (reserva != null) {

                if (reservaLogic.existsById(reserva.getId())) {

                    reservaLogic.updateReserva(reserva);
                    resposta = ResponseEntity.ok().build();
                } else {
                    resposta = ResponseEntity.notFound().build();
                }
            } else {
                resposta = ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("❌ Error actualitzant reserva amb ID: {}",
                    reserva != null ? reserva.getId() : "null", e);
            resposta = ResponseEntity.internalServerError().build();
        }
        return resposta;
    }


}
