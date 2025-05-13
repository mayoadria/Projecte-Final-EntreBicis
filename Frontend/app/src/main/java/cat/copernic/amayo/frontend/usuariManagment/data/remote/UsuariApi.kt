package cat.copernic.amayo.frontend.usuariManagment.data.remote

import cat.copernic.amayo.frontend.usuariManagment.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz que define las llamadas a la API REST relacionadas con la gestión de usuarios.
 * Utiliza Retrofit para realizar operaciones de autenticación, consulta y actualización de usuarios.
 */
interface UsuariApi {
    /**
     * Realiza la validación de un usuario mediante email y contraseña.
     *
     * @param email Dirección de correo del usuario.
     * @param contra Contraseña en texto plano.
     * @return Respuesta con el usuario autenticado si es válido.
     */
    @GET("validar/{email}/{contra}")
    suspend fun login(@Path("email") email : String, @Path("contra") contra : String): Response<Usuari>

    /**
     * Obtiene los datos de un usuario por su email.
     *
     * @param email Dirección de correo del usuario.
     * @return Respuesta con los datos del usuario encontrado.
     */
    @GET("getByEmail/{email}")
    suspend fun getByEmail(@Path("email") email: String): Response<Usuari>

    /**
     * Actualiza la información de un usuario.
     *
     * @param product Objeto Usuari con los datos a actualizar.
     * @return Respuesta sin cuerpo que indica éxito o fallo.
     */
    @PUT("update")
    suspend fun update(@Body product: Usuari): Response<Void>

    /**
     * Envía un correo electrónico al usuario especificado.
     *
     * @param email Dirección de correo del destinatario.
     * @return Respuesta sin cuerpo que indica éxito o fallo.
     */
    @POST("sendEmail/{email}")
    suspend fun sendEmail(@Path("email") email : String): Response<Void>

    /**
     * Valida un token de verificación recibido por email.
     *
     * @param token Código del token.
     * @param email Dirección de correo asociada al token.
     * @return Respuesta con valor booleano que indica si el token es válido.
     */
    @GET("validateToken/{token}/{email}")
    suspend fun validateToken(@Path("token") token : String, @Path("email") email : String ): Response<Boolean>
}
