package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa l'estat de validació d'una ruta dins del sistema.
 * Indica si una ruta enregistrada és considerada vàlida per a ser mostrada, processada o tenir efectes en el sistema.
 */
public enum EstatRutes {

    /**
     * La ruta ha estat validada i és considerada vàlida dins del sistema.
     */
    VALIDA,

    /**
     * La ruta ha estat invalidada i no es tindrà en compte per a processos o visualització oficial.
     */
    INVALIDA
}
