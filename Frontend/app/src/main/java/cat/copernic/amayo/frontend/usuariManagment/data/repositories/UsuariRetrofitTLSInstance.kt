package cat.copernic.amayo.frontend.usuariManagment.data.repositories

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Objeto singleton que proporciona una instancia de Retrofit con configuración TLS para la API de usuarios.
 * Esta configuración omite la verificación de certificados y hostname, lo que facilita las pruebas locales
 * pero no es recomendable para producción.
 */
object UsuariRetrofitTLSInstance {
    //private const val BASE_URL = "https://10.0.2.2:8443/api/usuari/"
    private const val BASE_URL = "https://entrebicis.hopto.org:8443/api/usuari/"

    /**
     * Instancia de Retrofit inicializada de forma perezosa con configuración TLS que acepta cualquier certificado.
     * Permite realizar llamadas HTTPS ignorando la validación de certificados y hostnames.
     */
    val retrofitTLSInstance: Retrofit by lazy {

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls<X509Certificate>(0)
            }
        }
        )
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())


        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { hostname: String?, session: SSLSession? -> true } // Acepta cualquier hostname (no recomendado en producción)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())  // Convierte JSON a objetos Kotlin
            .build()
    }
}