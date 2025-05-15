package com.copernic.backend.Backend.controller.android;


import com.copernic.backend.Backend.dto.PosicioDto;
import com.copernic.backend.Backend.dto.RutaDto;
import com.copernic.backend.Backend.entity.Posicio_Gps;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Sistema;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.logic.web.SistemaLogic;
import com.copernic.backend.Backend.logic.web.UsuariLogic;

import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.entity.enums.CicloRuta;
import com.copernic.backend.Backend.logic.android.RutesLogicAndroid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.catalina.manager.StatusTransformer.formatSeconds;

@RestController
@RequestMapping("/api/ruta")
public class ApiRutaController {

    @Autowired
    private RutesLogicAndroid rutesLogic;

    @Autowired
    private UsuariLogic usuariLogic;

    @Autowired
    private SistemaLogic sistemaLogic;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    /* ====================== API: guardar ruta ======================== */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> guardarRuta(@RequestBody RutaDto dto) {

        System.out.println(">>> MÉTODO guardarRuta ACTIVO");
        System.out.println(dto); // muestra todo lo que Jackson recibe

        // 1. Recuperamos el usuario y comprobamos que exista
        Usuari usuari = usuariLogic.findByEmail(dto.getEmailUsuari());
        if (usuari == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 2. Creamos la entidad Rutes y rellenamos campos básicos
        Rutes r = new Rutes();
        r.setUsuari(usuari);
        r.setNom(dto.getNom());
        r.setDescripcio(dto.getDescripcio());
        r.setEstat(dto.getEstat()        != null ? dto.getEstat()       : EstatRutes.INVALIDA);
        r.setCicloRuta(dto.getCicloRuta()!= null ? dto.getCicloRuta(): CicloRuta.FINALITZADA);

        // 3. Métricas kilométricas
        double kmRounded = 0.0;
        if (dto.getKm() != null) {
            kmRounded = Math.round(dto.getKm() * 1_000.0) / 1_000.0;
            r.setKm(kmRounded);
        }
        if (dto.getVelMitja()   != null) r.setVelMedia(dto.getVelMitja());
        if (dto.getVelMax()     != null) r.setVelMax(dto.getVelMax());
        if (dto.getVelMitjaKm() != null) r.setVelMitjaKM(dto.getVelMitjaKm());
        if (dto.getTempsParat() != null) r.setTempsParat(dto.getTempsParat().doubleValue());
        if (dto.getTemps()      != null) r.setTemps(formatSeconds(dto.getTemps()));
        // Eliminamos la copia directa de dto.getPunts()
        // if (dto.getPunts() != null) r.setPunts(dto.getPunts());

        // 4. Cálculo de puntos y actualización de saldo
        // ── SALDO CONVERSIÓN ─────────────────────────────────────
        if (kmRounded > 0.0) {
            Sistema sistema = sistemaLogic.getSistema();
            if (sistema != null && sistema.getConversioSaldo() > 0.0) {
                double factor = sistema.getConversioSaldo();
                // DEBUG: para verificar que esté leyendo bien el parámetro
                System.out.println(">>> conversioSaldo leída: " + factor);
                System.out.println(">>> DEBUG kmRounded: " + kmRounded);
                // puntos = factor × km, redondeado a 3 decimales
                double puntos = Math.round(factor * kmRounded * 1_000.0) / 1_000.0;
                System.out.println(">>> DEBUG puntos calculados: " + puntos);
                r.setPunts(puntos);
            }
        }


        // 5. Posiciones GPS (CascadeType.ALL)
        if (dto.getPosicions() != null) {
            List<Posicio_Gps> pos = dto.getPosicions().stream().map(p -> {
                Posicio_Gps pg = new Posicio_Gps();
                pg.setRutes(r);
                pg.setLatitud(p.getLatitud());
                pg.setLongitud(p.getLongitud());
                pg.setTemps(p.getTemps());
                return pg;
            }).toList();
            r.setPosicionsGps(pos);
        }

        // 6. Guardamos primero la ruta (que incluye ya puntos) y luego el usuario
        rutesLogic.save(r);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/usuari/{email}")
    public ResponseEntity<List<RutaDto>> getRoutesByUser(@PathVariable String email) {
        // 1) Validamos que exista el usuario
        usuariLogic.getUsuariByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        // 2) Recuperamos todas las entidades
        List<Rutes> rutas = rutesLogic.findByUsuariEmail(email);

        // 3) Mapeamos a DTO
        List<RutaDto> dtos = rutas.stream().map(r -> {
            RutaDto dto = new RutaDto();

            // — Campos básicos —
            dto.setId(r.getId());
            dto.setNom(r.getNom());
            dto.setDescripcio(r.getDescripcio());

            // — Métricas de la ruta —
            dto.setKm(r.getKm());

            // parseamos el String r.getTemps() a int
            // RAW = "180 s" o "  180 s  "
            String raw = r.getTemps();
            int segundos = 0;
            if (raw != null) {
                // 1) Quitamos lo que no sean dígitos
                String digits = raw.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    segundos = Integer.parseInt(digits);
                }
            }
            dto.setTemps(segundos);

            String rawParat = String.valueOf(r.getTempsParat());
            int parados = 0;
            if (rawParat != null) {
                String digitsParat = rawParat.replaceAll("[^0-9]", "");
                if (!digitsParat.isEmpty()) {
                    parados = Integer.parseInt(digitsParat);
                }
            }
            dto.setTempsParat(parados);


            dto.setVelMax(r.getVelMax());
            dto.setVelMitja(r.getVelMedia());
            dto.setVelMitjaKm(r.getVelMitjaKM());

            // — Estado y ciclo —
            dto.setEstat(r.getEstat());
            dto.setCicloRuta(r.getCicloRuta());

            // — Posiciones GPS —
            List<PosicioDto> posDto = r.getPosicionsGps().stream()
                    .map(p -> {
                        PosicioDto pd = new PosicioDto();
                        pd.setLatitud(p.getLatitud());
                        pd.setLongitud(p.getLongitud());
                        pd.setTemps(p.getTemps());
                        return pd;
                    })
                    .collect(Collectors.toList());
            dto.setPosicions(posDto);

            // — Metadata —
            dto.setEmailUsuari(email);
            dto.setFechaCreacion(r.getFechaCreacion().format(FMT));

            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }


}
