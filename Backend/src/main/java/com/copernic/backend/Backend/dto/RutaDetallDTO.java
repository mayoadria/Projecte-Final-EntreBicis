package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.Rutes;

import java.util.List;

/**
 * DTO (Data Transfer Object) per representar els detalls d'una ruta,
 * incloent la seva informació bàsica i les coordenades GPS.
 * Utilitzat per la vista per mostrar les rutes d'un usuari amb el seu recorregut.
 */
public class RutaDetallDTO {
    /** Entitat de la ruta amb la seva informació (nom, descripció, estat, etc.). */
    private final Rutes ruta;

    /**
     * Llista de coordenades GPS de la ruta.
     * Cada element és un array de {@code double[]} on:
     *     <li>{@code [0]} → Latitud
     *     {@code [1]} → Longitud
     *
     */
    private final List<double[]> coords;

    /**
     * Constructor per inicialitzar el DTO amb la ruta i les seves coordenades GPS.
     *
     * @param ruta Ruta amb la seva informació general.
     * @param coords Llista de coordenades GPS en format [latitud, longitud].
     */
    public RutaDetallDTO(Rutes ruta, List<double[]> coords) {
        this.ruta   = ruta;
        this.coords = coords;
    }

    /**
     * Obté la ruta associada al DTO.
     *
     * @return Objecte {@link Rutes} amb la informació de la ruta.
     */
    public Rutes getRuta()            { return ruta;   }
    /**
     * Obté la llista de coordenades GPS de la ruta.
     *
     * @return Llista de {@code double[]} amb latitud i longitud.
     */
    public List<double[]> getCoords() { return coords; }
}