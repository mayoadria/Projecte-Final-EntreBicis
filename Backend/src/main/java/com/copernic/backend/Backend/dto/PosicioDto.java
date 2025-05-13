package com.copernic.backend.Backend.dto;

/**
 * DTO (Data Transfer Object) per representar una posició GPS en una ruta.
 * Inclou la latitud, longitud i el temps en segons des de l'inici de la ruta.
 * Utilitzat per transferir dades entre la capa de presentació i la lògica de negoci.
 */
public class PosicioDto {

    /**
     * Coordenada de latitud en graus decimals.
     */
    private Double latitud;

    /**
     * Coordenada de longitud en graus decimals.
     */
    private Double longitud;

    /**
     * Temps en segons des de l'inici de la ruta fins a aquesta posició.
     */
    private Integer temps;

    /**
     * Constructor buit requerit per a la deserialització JSON.
     */
    public PosicioDto() { }

    /**
     * Obté la latitud de la posició.
     *
     * @return Latitud en graus decimals.
     */
    public Double getLatitud() { return latitud; }

    /**
     * Estableix la latitud de la posició.
     *
     * @param latitud Valor en graus decimals.
     */
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    /**
     * Obté la longitud de la posició.
     *
     * @return Longitud en graus decimals.
     */
    public Double getLongitud() { return longitud; }

    /**
     * Estableix la longitud de la posició.
     *
     * @param longitud Valor en graus decimals.
     */
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    /**
     * Obté el temps associat a la posició (en segons des de l'inici de la ruta).
     *
     * @return Temps en segons.
     */
    public Integer getTemps() { return temps; }

    /**
     * Estableix el temps associat a la posició (en segons des de l'inici de la ruta).
     *
     * @param temps Valor en segons.
     */
    public void setTemps(Integer temps) { this.temps = temps; }
}
