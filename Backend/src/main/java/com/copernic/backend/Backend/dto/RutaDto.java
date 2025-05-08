package com.copernic.backend.Backend.dto;

import com.copernic.backend.Backend.entity.enums.EstatRutes;
import com.copernic.backend.Backend.entity.enums.CicloRuta;

import java.util.List;

public class RutaDto {
    private Long id;
    private String nom;
    private String descripcio;
    private List<PosicioDto> posicions;
    private EstatRutes estat;
    private CicloRuta cicloRuta;
    private String emailUsuari;

    public RutaDto() { }

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getNom()                   { return nom; }
    public void setNom(String nom)           { this.nom = nom; }

    public String getDescripcio()            { return descripcio; }
    public void setDescripcio(String d)      { this.descripcio = d; }

    public List<PosicioDto> getPosicions()   { return posicions; }
    public void setPosicions(List<PosicioDto> p) { this.posicions = p; }

    public EstatRutes getEstat()             { return estat; }
    public void setEstat(EstatRutes e)       { this.estat = e; }

    public CicloRuta getCicloRuta()          { return cicloRuta; }
    public void setCicloRuta(CicloRuta c)    { this.cicloRuta = c; }

    public String getEmailUsuari() { return emailUsuari; }
    public void   setEmailUsuari(String e) { this.emailUsuari = e; }
}
