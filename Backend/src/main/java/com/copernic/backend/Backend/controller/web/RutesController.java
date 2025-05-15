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
import com.copernic.backend.Backend.logic.web.UsuariLogic;
import com.copernic.backend.Backend.repository.RutesRepository;
import com.copernic.backend.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    private final UsuariLogic usuariLogic;

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


    /**
     * Cambia el estado (VALIDA/INVALIDA) de una ruta.
     */
    @PutMapping(value = "/ruta/{id}/estat", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void updateEstatRuta(
            @PathVariable Long id,
            @RequestBody EstatRutes nouEstat
    ) {
        Rutes ruta = rutesLogic.getRutaById(id);
        if (ruta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no trobada: " + id);
        }

        EstatRutes estatAntic = ruta.getEstat();
        Usuari usuari = ruta.getUsuari();
        double puntosRuta = ruta.getPunts() != null ? ruta.getPunts() : 0.0;
        double saldoActual = usuari.getSaldo();

        // 1) Pasar a VALIDADA: sumamos sólo una vez
        if (estatAntic != EstatRutes.VALIDA && nouEstat == EstatRutes.VALIDA) {
            usuari.setSaldo(saldoActual + puntosRuta);
            usuariLogic.savePerfil(usuari);
        }
        // 2) Pasar de VALIDADA a otro estado: restamos sólo si hay saldo suficiente
        else if (estatAntic == EstatRutes.VALIDA && nouEstat != EstatRutes.VALIDA) {
            double resta = Math.min(saldoActual, puntosRuta);
            usuari.setSaldo(saldoActual - resta);
            usuariLogic.savePerfil(usuari);
        }

        // 3) Finalmente, actualizamos y guardamos el nuevo estado de la ruta
        ruta.setEstat(nouEstat);
        rutesLogic.saveRuta(ruta);
    }


}
