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
    private static final Logger logger = LoggerFactory.getLogger(ApiReservaController.class);
    @Autowired
    private ReservaLogic reservaLogic;

    @PostConstruct
    private void init() {
    }

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
