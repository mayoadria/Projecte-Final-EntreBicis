package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa l'estat d'activació d'un usuari dins del sistema.
 * Controla si un usuari pot accedir i utilitzar les funcionalitats o està inhabilitat.
 */
public enum EstatUsuari {

    /**
     * L'usuari està actiu i pot utilitzar totes les funcionalitats del sistema.
     */
    ACTIU,

    /**
     * L'usuari està inactiu i no pot accedir a les funcionalitats del sistema.
     */
    INACTIU
}
