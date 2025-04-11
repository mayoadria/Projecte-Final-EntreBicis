package com.copernic.backend.Backend.controller.android;

import com.copernic.backend.Backend.entity.Reserva;
import com.copernic.backend.Backend.logic.web.ReservaLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
