package cat.copernic.amayo.frontend.recompensaManagment.data.repositories

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton que proporciona una instància segura de [Retrofit] per accedir a l'API de recompenses via HTTPS.
 *
 *
 * Aquesta implementació configura un client TLS que accepta qualsevol certificat (ús només recomanat en entorns de desenvolupament).
 * S'utilitza per fer crides a l'API de recompenses en un servidor local o remot amb HTTPS.
 * Inclou suport per serialitzar i deserialitzar [LocalDateTime] en el format `yyyy-MM-dd HH:mm:ss`.
 *
 *
 * ATENCIÓ:
 * Acceptar qualsevol certificat i hostname és una pràctica insegura per a entorns de producció.
 * Aquesta configuració és vàlida només per proves locals (localhost/10.0.2.2).
 *
 * @property retrofitTLSInstance Instància única de [Retrofit] inicialitzada de forma lazy amb configuració TLS relaxada.
 */
object RecompensaRetrofitTLSInstance {
    private const val BASE_URL = "https://10.0.2.2:8443/api/recompensa/"
    //private const val BASE_URL = "https://entrebicis.hopto.org:8443/api/recompensa/"

    /**
     * Instància de [Retrofit] configurada per a connexions HTTPS sense validació estricta de certificats.
     * Inclou un adaptador personalitzat per a [LocalDateTime].
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

         val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                LocalDateTimeAdapter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            .create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))  // Convierte JSON a objetos Kotlin
            .build()
    }
}