package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Recompensas;
import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reserva")
public class ApiReservaController {
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

                response = ResponseEntity.notFound().build();
            } else {

                prodId = reservaLogic.saveReserva(reserva);
                response = new ResponseEntity<>(prodId, HttpStatus.CREATED);

            }

        } catch (Exception e) {

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
                response = new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            } else {
                response = new ResponseEntity<>(reserva, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
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

            response = new ResponseEntity<>(reservas, headers, HttpStatus.OK);
        } catch (Exception e) {
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

            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }



}
