package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.entity.enums.CicloRuta;

import java.util.List;

/**
 * DTO que rep el backend des de l’app Android.
 */

public class RutaDto {

    /** Identificador únic de la ruta. */
    private Long id;

    /** Nom de la ruta. */
    private String nom;

    /** Descripció breu de la ruta. */
    private String descripcio;

    /** Llista de posicions GPS que formen el recorregut de la ruta. */
    private List<PosicioDto> posicions;

    /** Estat actual de la ruta (VALIDA, INVALIDA, etc.). */
    private EstatRutes estat;

    /** Etapa del cicle de la ruta (EN CURS, FINALITZADA, etc.). */
    private CicloRuta cicloRuta;

    /** Correu electrònic de l'usuari propietari de la ruta. */
    private String emailUsuari;

    /** Distància total de la ruta en quilòmetres. */
    private Double km;
    private Integer temps;        // segons totals
    private Integer tempsParat;   // segons parat
    private Double velMax;        // km/h
    private Double velMitja;      // km/h
    private Double velMitjaKm;    // min/km
    private Double punts;    // min/km

    private String fechaCreacion;

    public RutaDto() {
    }

    public Double getPunts() {
        return punts;
    }

    public void setPunts(Double punts) {
        this.punts = punts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String d) {
        this.descripcio = d;
    }

    public List<PosicioDto> getPosicions() {
        return posicions;
    }

    public void setPosicions(List<PosicioDto> p) {
        this.posicions = p;
    }

    public EstatRutes getEstat() {
        return estat;
    }

    public void setEstat(EstatRutes e) {
        this.estat = e;
    }
    /** @return Temps total aturat en segons. */
    public Integer getTempsParat() { return tempsParat; }

    /** @param t Temps aturat en segons. */
    public void setTempsParat(Integer t) { this.tempsParat = t; }

    public CicloRuta getCicloRuta() {
        return cicloRuta;
    }
    /** @return Velocitat màxima de la ruta en km/h. */
    public Double getVelMax() { return velMax; }

    /** @param v Velocitat màxima en km/h. */
    public void setVelMax(Double v) { this.velMax = v; }

    public void setCicloRuta(CicloRuta c) {
        this.cicloRuta = c;
    }
    /** @return Velocitat mitjana en km/h. */
    public Double getVelMitja() { return velMitja; }

    /** @param v Velocitat mitjana en km/h. */
    public void setVelMitja(Double v) { this.velMitja = v; }

    public String getEmailUsuari() {
        return emailUsuari;
    }

    public void setEmailUsuari(String e) {
        this.emailUsuari = e;
    }


    public Double getKm() {
        return km;
    }

    public void setKm(Double k) {
        this.km = k;
    }

    public Integer getTemps() {
        return temps;
    }

    public void setTemps(Integer t) {
        this.temps = t;
    }

    public Integer getTempsParat() {
        return tempsParat;
    }

    public void setTempsParat(Integer t) {
        this.tempsParat = t;
    }

    public Double getVelMax() {
        return velMax;
    }

    public void setVelMax(Double v) {
        this.velMax = v;
    }

    public Double getVelMitja() {
        return velMitja;
    }

    public void setVelMitja(Double v) {
        this.velMitja = v;
    }

    public Double getVelMitjaKm() {
        return velMitjaKm;
    }

    public void setVelMitjaKm(Double v) {
        this.velMitjaKm = v;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    /** @return Velocitat mitjana en min/km. */
    public Double getVelMitjaKm() { return velMitjaKm; }

    /** @param v Velocitat mitjana en min/km. */
    public void setVelMitjaKm(Double v) { this.velMitjaKm = v; }

}
