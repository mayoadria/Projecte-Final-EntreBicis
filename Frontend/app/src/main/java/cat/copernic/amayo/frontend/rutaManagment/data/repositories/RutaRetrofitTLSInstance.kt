package cat.copernic.amayo.frontend.rutaManagment.data.repositories

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor           // ← NUEVO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RutaRetrofitTLSInstance {

    private const val BASE_URL = "https://10.0.2.2:8443/api/"
    //private const val BASE_URL = "https://entrebicis.hopto.org:8443/api/"
    val retrofitTLSInstance: Retrofit by lazy {

        /* ── Trust-all SSL (ya lo tenías) ─────────────────────────────── */
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
        })
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        /* ── Interceptor de LOG (muestra URL + JSON) ──────────────────── */
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        /* ── OkHttpClient con TLS + logging ───────────────────────────── */
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)                                    // ← NUEVO
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _: String?, _: SSLSession? -> true }    // (no usar esto en prod)
            .build()

        /* ── Retrofit ─────────────────────────────────────────────────── */
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
