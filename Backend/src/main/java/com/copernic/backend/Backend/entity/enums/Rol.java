package com.copernic.backend.Backend.entity.enums;

/**
 * Enum que representa els diferents rols d'usuari dins del sistema.
 * Els rols determinen les funcionalitats i permisos accessibles per cada usuari.
 */
public enum Rol {

    /**
     * Usuari amb rol de CICLISTA.
     * Pot realitzar accions bàsiques com visualitzar i gestionar les seves rutes i recompenses.
     */
    CICLISTA,

    /**
     * Usuari amb rol d'ADMINISTRADOR.
     * Té accés a totes les funcionalitats del sistema, inclosa la gestió d'usuaris, recompenses i rutes.
     */
    ADMINISTRADOR
}

