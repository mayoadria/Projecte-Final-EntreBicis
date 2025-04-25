package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import lombok.Getter;

@Getter
public class RouteCardDTO {

    private final String  title;
    private final String  distance;   // 2,55
    private final String  points;     // 2,5
    private final String  time;       // 10:00
    private final String  avgSpeed;   // 15
    private final String  mapUrl;     // URL mapa
    private final String  userName;   // “Nom Cognom”
    private final boolean active;     // on/off

    public RouteCardDTO(Usuari user, Rutes ruta) {

        this.userName = user.getNom() + " " + user.getCognom();

        /* ───── Usuario sin rutas ─────────────────────────────── */
        if (ruta == null) {
            this.title     = "Sense ruta";
            this.distance  = "-";
            this.points    = "-";
            this.time      = "-";
            this.avgSpeed  = "-";
            this.mapUrl    = "/img/map-placeholder.png";
            this.active    = false;
            return;
        }

        /* ───── Ruta existente ───────────────────────────────── */
        this.title     = ruta.getNom();

        // km es int ⇒ cásting a double para usar %f  (ya no lanza f!=Integer)
        this.distance  = String.format("%.2f", (double) ruta.getKm());

        this.points    = String.format("%.1f", calcularPunts(ruta));
        this.time      = ruta.getTemps();

        // velMedia puede ser null
        this.avgSpeed  = ruta.getVelMedia() != null
                ? String.format("%.0f", ruta.getVelMedia())
                : "-";

        this.mapUrl    = generaUrlMapa(ruta);
        this.active    = true;
    }

    /* -------- helpers ---------------------------------------- */

    private double calcularPunts(Rutes r) {
        return r.getKm();                  // tu lógica real aquí
    }

    private String generaUrlMapa(Rutes r) {
        return "/img/map-placeholder.png"; // genera URL real aquí
    }
}