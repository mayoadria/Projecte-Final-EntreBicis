package com.copernic.backend.Backend.controller.web;

import com.copernic.backend.Backend.dto.RouteCardDTO;
import com.copernic.backend.Backend.dto.RutaDetallDTO;
import com.copernic.backend.Backend.dto.RutaDto;
import com.copernic.backend.Backend.entity.Posicio_Gps;
import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import com.copernic.backend.Backend.entity.enums.CicloRuta;
import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.logic.web.RutesLogic;
import com.copernic.backend.Backend.repository.RutesRepository;
import com.copernic.backend.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.catalina.manager.StatusTransformer.formatSeconds;

/**
 * Vista de administración que muestra, como máximo, un “card” por usuario
 * con su última ruta (o vacío si aún no ha salido en bici).  Límite 50 usuarios.
 */
@Controller
@RequiredArgsConstructor
public class RutesController {

    private final UserRepository usuariRepository;
    private final RutesLogic       rutesLogic;
    private final RutesRepository rutesRepository;

    /* ======================= Vista de targetes ======================= */
    @GetMapping("/admin/rutes")
    public String rutes(Model model) {

        // 1. Traemos la lista (máx. 50 usuarios)
        List<Usuari> usuaris = usuariRepository.findAll();
        if (usuaris.size() > 50) {
            usuaris = usuaris.subList(0, 50);
        }

        // 2. Para cada usuario buscamos su última ruta (puede devolver null)
        List<RouteCardDTO> cards = usuaris.stream()
                .map(u -> {
                    Rutes last = rutesLogic.findLastByUsuari(u);   // nuevo método en la lógica
                    return new RouteCardDTO(u, last);              // DTO con toda la info que tu plantilla necesita
                })
                .collect(Collectors.toList());

        // 3. Enviamos a la vista Thymeleaf
        model.addAttribute("routeCards", cards);
        return "rutes";   // templates/rutes.html
    }


    /* ================= Detall + llista de rutes de l’usuari =============== */
    @GetMapping("/ruta/detalls/{id}")
    public String detallsRuta(@PathVariable Long id,
                              Model model,
                              RedirectAttributes ra) {

        /* 1. Ruta que ha clicat l’usuari */
        Rutes sel = rutesLogic.getRutaById(id);
        if (sel == null) {
            ra.addFlashAttribute("error", "La ruta seleccionada no existeix.");
            return "redirect:/admin/rutes";
        }

        /* 2. TOTES les rutes del mateix usuari (ordre desc) */
        List<Rutes> rutes = rutesLogic.findAllByUsuari(sel.getUsuari());

        /* 3. Convertim cada ruta en DTO amb les seves coordenades */
        List<RutaDetallDTO> dtos = rutes.stream()
                .map(r -> new RutaDetallDTO(
                        r,
                        r.getPosicionsGps().stream()
                                .sorted(Comparator.comparingLong(Posicio_Gps::getId))
                                .map(p -> new double[]{p.getLatitud(), p.getLongitud()})
                                .toList()
                ))
                .toList();

        /* 4. Només aquest atribut és necessari per a la vista */
        model.addAttribute("rutes", dtos);
        return "detallsRuta";
    }


    /* ====================== API: guardar ruta ======================== */
    @PostMapping(value = "/api/ruta", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> guardarRuta(@RequestBody RutaDto dto) {

        System.out.println(">>> MÉTODO guardarRuta ACTIVO");
        System.out.println(dto); // muestra todo lo que Jackson recibe

        Usuari usuari = usuariRepository.findByEmail(dto.getEmailUsuari());
        if (usuari == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Rutes r = new Rutes();
        r.setUsuari(usuari);
        r.setNom(dto.getNom());
        r.setDescripcio(dto.getDescripcio());
        r.setEstat(dto.getEstat()      != null ? dto.getEstat()      : EstatRutes.INVALIDA);
        r.setCicloRuta(dto.getCicloRuta()!= null ? dto.getCicloRuta() : CicloRuta.FINALITZADA);

        /* ── MÉTRICAS ─────────────────────────────────────────── */
        if (dto.getKm() != null) {
            // redondear a 3 decimales exactos antes de guardar
            double kmRounded = Math.round(dto.getKm() * 1_000.0) / 1_000.0;
            r.setKm(kmRounded);
        }
        if (dto.getVelMitja()   != null) r.setVelMedia(dto.getVelMitja());
        if (dto.getVelMax()     != null) r.setVelMax(dto.getVelMax());
        if (dto.getVelMitjaKm() != null) r.setVelMitjaKM(dto.getVelMitjaKm());
        if (dto.getTempsParat() != null) r.setTempsParat(dto.getTempsParat().doubleValue());
        if (dto.getTemps()      != null) r.setTemps(formatSeconds(dto.getTemps()));

        /* Posicions GPS → CascadeType.ALL */
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

        rutesRepository.save(r);        // guarda ruta + posicions
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
