package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.Rutes;

import java.util.List;

public class RutaDetallDTO {

    private final Rutes ruta;
    private final List<double[]> coords;

    public RutaDetallDTO(Rutes ruta, List<double[]> coords) {
        this.ruta   = ruta;
        this.coords = coords;
    }

    public Rutes getRuta()            { return ruta;   }
    public List<double[]> getCoords() { return coords; }
}