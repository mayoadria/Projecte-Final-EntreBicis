package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.Rutes;
import com.copernic.backend.Backend.entity.Usuari;
import lombok.Getter;

/**
 * DTO (Data Transfer Object) per representar la informació resumida d'una ruta
 * en una targeta visual a la vista d'administració.
 * Conté dades de l'usuari i de la seva última ruta per mostrar en un card estilitzat.
 * En cas que l'usuari no tingui rutes, mostra valors per defecte.
 */
@Getter
public class RouteCardDTO {

    /**
     * Títol de la ruta o "Sense ruta" si no en té.
     */
    private final String title;

    /**
     * Correu electrònic de l'usuari.
     */
    private final String userEmail;

    /**
     * Distància de la ruta en quilòmetres (formatat a 3 decimals).
     */
    private final String distance;

    /**
     * Punts obtinguts per la ruta (formatat a 1 decimal).
     */
    private final String points;

    /**
     * Temps total de la ruta en format hh:mm:ss.
     */
    private final String time;

    /**
     * Velocitat mitjana en km/h (sense decimals).
     */
    private final String avgSpeed;

    /**
     * URL de la imatge del mapa de la ruta.
     */
    private final String mapUrl;

    /**
     * Nom complet de l'usuari (Nom Cognom).
     */
    private final String userName;

    /**
     * Indica si la targeta té una ruta activa.
     */
    private final boolean active;

    /**
     * ID de la ruta (null si no existeix).
     */
    private final Long id;

    /**
     * Constructor que inicialitza el DTO amb les dades de l'usuari i la seva última ruta.
     * Si {@code ruta} és null, es generen valors per defecte per indicar que no hi ha cap ruta.
     *
     * @param user Usuari propietari de la ruta.
     * @param ruta Última ruta registrada per l'usuari (pot ser null).
     */
    public RouteCardDTO(Usuari user, Rutes ruta) {

        this.userName = user.getNom() + " " + user.getCognom();
        this.userEmail = user.getEmail();

        /* ───── Usuario sin rutas ─────────────────────────────── */
        if (ruta == null) {
            this.title = "Sense ruta";
            this.distance = "-";
            this.points = "-";
            this.time = "-";
            this.avgSpeed = "-";
            this.mapUrl = "/img/map-placeholder.png";
            this.active = false;
            this.id = null;
            return;
        }

        /* ───── Ruta existente ───────────────────────────────── */
        this.title = ruta.getNom();

        // km es int ⇒ cásting a double para usar %f
        this.distance = String.format("%.3f", (double) ruta.getKm());

        this.points = String.format("%.1f", calcularPunts(ruta));
        this.time = ruta.getTemps();

        // velMedia puede ser null
        this.avgSpeed = ruta.getVelMedia() != null
                ? String.format("%.0f", ruta.getVelMedia())
                : "-";

        this.mapUrl = generaUrlMapa(ruta);
        this.active = true;
        this.id = ruta.getId();
    }

    /* -------- helpers ---------------------------------------- */

    /**
     * Calcula els punts obtinguts per la ruta.
     * En aquest exemple retorna la distància com a punts, però la lògica es pot adaptar.
     *
     * @param r Ruta per calcular els punts.
     * @return Punts obtinguts.
     */
    private double calcularPunts(Rutes r) {
        return r.getKm();                  // tu lógica real aquí
    }

    /**
     * Genera la URL de la imatge del mapa per la ruta.
     * En aquest exemple retorna un placeholder, però es pot personalitzar per a generar URL reals.
     *
     * @param r Ruta per la qual es vol generar la imatge.
     * @return URL de la imatge del mapa.
     */
    private String generaUrlMapa(Rutes r) {
        return "/img/map-placeholder.png"; // genera URL real aquí
    }
}
