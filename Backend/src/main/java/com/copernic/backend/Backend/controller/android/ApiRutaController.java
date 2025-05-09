package com.copernic.backend.Backend.controller.android;


import com.copernic.backend.Backend.dto.PosicioDto;
import com.copernic.backend.Backend.dto.RutaDto;
import com.copernic.backend.Backend.entity.Posicio_Gps;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.entity.enums.CicloRuta;
import com.copernic.backend.Backend.logic.android.RutesLogicAndroid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ruta")
public class ApiRutaController {

    private static final Logger logger = LoggerFactory.getLogger(ApiRutaController.class);
    @Autowired
    private RutesLogicAndroid rutesLogic;

    @Autowired
    private UsuariLogic usuariLogic;


    @PostMapping
    public ResponseEntity<?> createRutaConPosicions(@RequestBody RutaDto rutaDto) {
        try {
            // Validación básica del DTO
            if (rutaDto == null || rutaDto.getEmailUsuari() == null) {
                logger.warn("❌ RutaDto o email null en la solicitud");
                return ResponseEntity.badRequest().body("Dades de la ruta no vàlides.");
            }

            // Construcción de entidad base
            Rutes ruta = Rutes.builder()
                    .nom(rutaDto.getNom())
                    .descripcio(rutaDto.getDescripcio())
                    .estat(EstatRutes.INVALIDA)
                    .cicloRuta(CicloRuta.INICIADA)
                    .build();

            // Asociación de usuario
            Usuari usuari = usuariLogic.getUsuariByEmail(rutaDto.getEmailUsuari())
                    .orElse(null);
            if (usuari == null) {
                logger.warn("⚠️ Usuari no trobat: {}", rutaDto.getEmailUsuari());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuari inexistent.");
            }
            ruta.setUsuari(usuari);

            // Mapeo de posiciones GPS
            List<Posicio_Gps> posList = rutaDto.getPosicions().stream()
                    .map(pd -> {
                        Posicio_Gps p = new Posicio_Gps();
                        p.setLatitud(pd.getLatitud());
                        p.setLongitud(pd.getLongitud());
                        p.setTemps(pd.getTemps());
                        p.setRutes(ruta);
                        return p;
                    }).collect(Collectors.toList());
            ruta.setPosicionsGps(posList);

            // Guardado en base de datos
            Rutes guardada = rutesLogic.save(ruta);
            logger.info("✅ Ruta guardada amb ID {}", guardada.getId());

            // Construcción de respuesta DTO
            RutaDto resp = new RutaDto();
            resp.setId(guardada.getId());
            resp.setNom(guardada.getNom());
            resp.setDescripcio(guardada.getDescripcio());
            resp.setEstat(guardada.getEstat());
            resp.setCicloRuta(guardada.getCicloRuta());
            resp.setPosicions(
                    guardada.getPosicionsGps().stream().map(p -> {
                        PosicioDto dto = new PosicioDto();
                        dto.setLatitud(p.getLatitud());
                        dto.setLongitud(p.getLongitud());
                        dto.setTemps(p.getTemps());
                        return dto;
                    }).collect(Collectors.toList())
            );

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            logger.error("❌ Error creant la ruta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error intern al crear la ruta.");
        }
    }
}
