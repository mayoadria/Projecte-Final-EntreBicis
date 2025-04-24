package com.copernic.backend.Backend.dto;

public class PosicioDto {
    private Double latitud;
    private Double longitud;
    private Integer temps;

    public PosicioDto() { }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Integer getTemps() { return temps; }
    public void setTemps(Integer temps) { this.temps = temps; }
}
