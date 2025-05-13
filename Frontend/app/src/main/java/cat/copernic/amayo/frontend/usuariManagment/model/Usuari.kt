package cat.copernic.amayo.frontend.usuariManagment.model

import cat.copernic.amayo.frontend.rutaManagment.model.Estat


/**
 * Datos del usuario de la aplicación.
 *
 * @property email Dirección de correo electrónico del usuario (identificador único).
 * @property nom Nombre del usuario.
 * @property cognom Apellido del usuario.
 * @property contra Contraseña del usuario.
 * @property telefon Número de teléfono del usuario.
 * @property poblacio Localidad de residencia del usuario.
 * @property saldo Saldo de puntos disponibles del usuario.
 * @property rol Rol asignado al usuario (CICLISTA o ADMINISTRADOR).
 * @property estat Estado actual del usuario (ACTIU o INACTIU).
 * @property foto Imagen de perfil en formato Base64.
 * @property reserva Indica si el usuario tiene una reserva activa.
 */
data class Usuari (
    var email: String? = null,
    var nom: String? = null,
    var cognom: String? = null,
    var contra: String? = null,
    var telefon: String? = null,
    var poblacio: String? = null,
    var saldo: Double? = null,
    var rol: Rol? = null,
    var estat: Estat? = null,
    var foto: String? = null,
    var reserva: Boolean = false
)