package cat.copernic.amayo.frontend.usuariManagment.model

/**
 * Enum que representa los diferentes roles que puede tener un usuario en la aplicación.
 *
 * @property CICLISTA Usuario con permisos estándar para utilizar las funcionalidades básicas.
 * @property ADMINISTRADOR Usuario con permisos elevados para gestionar la aplicación y sus recursos.
 */
enum class Rol {
    CICLISTA,ADMINISTRADOR
}