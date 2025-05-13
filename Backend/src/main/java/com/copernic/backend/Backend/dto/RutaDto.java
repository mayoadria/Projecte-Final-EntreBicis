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

    /** Temps total de la ruta en segons. */
    private Integer temps;

    /** Temps total aturat durant la ruta (en segons). */
    private Integer tempsParat;

    /** Velocitat màxima registrada durant la ruta (en km/h). */
    private Double velMax;

    /** Velocitat mitjana de la ruta (en km/h). */
    private Double velMitja;

    /** Velocitat mitjana expressada en min/km. */
    private Double velMitjaKm;

    /**
     * Constructor buit requerit per a la deserialització JSON.
     */
    public RutaDto() { }


    /** @return ID de la ruta. */
    public Long getId() { return id; }

    /** @param id ID de la ruta. */
    public void setId(Long id) { this.id = id; }

    /** @return Nom de la ruta. */
    public String getNom() { return nom; }

    /** @param nom Nom de la ruta. */
    public void setNom(String nom) { this.nom = nom; }

    /** @return Descripció de la ruta. */
    public String getDescripcio() { return descripcio; }

    /** @param d Descripció de la ruta. */
    public void setDescripcio(String d) { this.descripcio = d; }

    /** @return Llista de posicions GPS de la ruta. */
    public List<PosicioDto> getPosicions() { return posicions; }

    /** @param p Llista de posicions GPS de la ruta. */
    public void setPosicions(List<PosicioDto> p) { this.posicions = p; }

    /** @return Estat actual de la ruta. */
    public EstatRutes getEstat() { return estat; }

    /** @param e Estat de la ruta. */
    public void setEstat(EstatRutes e) { this.estat = e; }

    /** @return Cicle de la ruta. */
    public CicloRuta getCicloRuta() { return cicloRuta; }

    /** @param c Cicle de la ruta. */
    public void setCicloRuta(CicloRuta c) { this.cicloRuta = c; }

    /** @return Email de l'usuari propietari de la ruta. */
    public String getEmailUsuari() { return emailUsuari; }

    /** @param e Email de l'usuari. */
    public void setEmailUsuari(String e) { this.emailUsuari = e; }

    /** @return Distància total de la ruta en km. */
    public Double getKm() { return km; }

    /** @param k Distància total de la ruta en km. */
    public void setKm(Double k) { this.km = k; }

    /** @return Temps total de la ruta en segons. */
    public Integer getTemps() { return temps; }

    /** @param t Temps total de la ruta en segons. */
    public void setTemps(Integer t) { this.temps = t; }

    /** @return Temps total aturat en segons. */
    public Integer getTempsParat() { return tempsParat; }

    /** @param t Temps aturat en segons. */
    public void setTempsParat(Integer t) { this.tempsParat = t; }

    /** @return Velocitat màxima de la ruta en km/h. */
    public Double getVelMax() { return velMax; }

    /** @param v Velocitat màxima en km/h. */
    public void setVelMax(Double v) { this.velMax = v; }

    /** @return Velocitat mitjana en km/h. */
    public Double getVelMitja() { return velMitja; }

    /** @param v Velocitat mitjana en km/h. */
    public void setVelMitja(Double v) { this.velMitja = v; }

    /** @return Velocitat mitjana en min/km. */
    public Double getVelMitjaKm() { return velMitjaKm; }

    /** @param v Velocitat mitjana en min/km. */
    public void setVelMitjaKm(Double v) { this.velMitjaKm = v; }

}
