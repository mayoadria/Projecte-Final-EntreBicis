package cat.copernic.amayo.frontend.usuariManagment.model


/**
 * Datos del sistema de configuración general.
 *
 * @property id Identificador único del sistema.
 * @property velMax Velocidad máxima permitida (en km/h).
 * @property tempsMaxAturat Tiempo máximo permitido en parada (en minutos).
 * @property Conversiosaldo Factor de conversión de saldo para recompensas.
 * @property tempsRecollida Tiempo máximo de recogida (en formato de texto, por ejemplo "72H").
 */
data class Sistema (
    var id: Long? = null,
    var velMax: Double? = null,
    var tempsMaxAturat: Double? = null,
    var Conversiosaldo: Int? = null,
    var tempsRecollida: String? = null,
)