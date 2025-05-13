package cat.copernic.amayo.frontend.recompensaManagment.data.repositories

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
 * Singleton que proporciona una instància segura de Retrofit per accedir a l'API de reserves via HTTPS.
 *
 * Aquesta implementació configura un client TLS que accepta qualsevol certificat i hostname.
 * Està pensada per a entorns de desenvolupament on es fa servir un servidor local o proves amb certificats no vàlids.
 *
 * La IP 10.0.2.2 permet accedir al localhost de la màquina host des de l'emulador d'Android.
 * La connexió es fa al port 8443 per HTTPS.
 *
 * Important: Acceptar tots els certificats i hostnames no és segur en producció.
 *
 * @property retrofitTLSInstance Instància única de Retrofit configurada amb TLS relaxat per a l'API de reserves.
 */
object ReservaRetrofitTLSInstance {
    //private const val BASE_URL = "https://10.0.2.2:8443/api/reserva/"
    private const val BASE_URL = "https://entrebicis.hopto.org:8443/api/reserva/"


    /**
     * Crea i retorna una instància de Retrofit configurada per ignorar la validació de certificats i hostname.
     * Aquesta instància es crea només la primera vegada que s'accedeix.
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