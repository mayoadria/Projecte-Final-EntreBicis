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

    @Autowired
    private RutesLogicAndroid rutesLogic;

    @Autowired
    private UsuariLogic usuariLogic;


    @PostMapping
    public ResponseEntity<RutaDto> createRutaConPosicions(@RequestBody RutaDto rutaDto) {

        // 1) Construir la entidad base
        Rutes ruta = Rutes.builder()
                .nom(rutaDto.getNom())
                .descripcio(rutaDto.getDescripcio())
                .estat(EstatRutes.INVALIDA)
                .cicloRuta(CicloRuta.INICIADA)
                .build();

        // 2) Vincular usuario usando el email recibido
        Usuari u = usuariLogic.getUsuariByEmail(rutaDto.getEmailUsuari())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Usuari inexistent"));
        ruta.setUsuari(u);


        // 3) Mapear posiciones
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

        // 4) Guardar en cascada
        Rutes guardada = rutesLogic.save(ruta);

        // 5) Devolver DTO de respuesta (sin cambios)
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
    }
}
