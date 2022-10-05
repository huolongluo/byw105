package com.legend.modular_contract_sdk.component.net

import com.legend.modular_contract_sdk.BuildConfig
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.utils.SPUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object RetrofitInstance {

    val CER = "-----BEGIN CERTIFICATE-----\n" +
            "    MIIFRjCCBC6gAwIBAgIGAXS+uhLQMA0GCSqGSIb3DQEBCwUAMIGnMTgwNgYDVQQD\n" +
            "    DC9DaGFybGVzIFByb3h5IENBICgyNCBTZXAgMjAyMCwgTEFQVE9QLU9SUkVKS1NS\n" +
            "    KTElMCMGA1UECwwcaHR0cHM6Ly9jaGFybGVzcHJveHkuY29tL3NzbDERMA8GA1UE\n" +
            "    CgwIWEs3MiBMdGQxETAPBgNVBAcMCEF1Y2tsYW5kMREwDwYDVQQIDAhBdWNrbGFu\n" +
            "    ZDELMAkGA1UEBhMCTlowHhcNMDAwMTAxMDAwMDAwWhcNNDkxMTIxMDYwODE5WjCB\n" +
            "    pzE4MDYGA1UEAwwvQ2hhcmxlcyBQcm94eSBDQSAoMjQgU2VwIDIwMjAsIExBUFRP\n" +
            "    UC1PUlJFSktTUikxJTAjBgNVBAsMHGh0dHBzOi8vY2hhcmxlc3Byb3h5LmNvbS9z\n" +
            "    c2wxETAPBgNVBAoMCFhLNzIgTHRkMREwDwYDVQQHDAhBdWNrbGFuZDERMA8GA1UE\n" +
            "    CAwIQXVja2xhbmQxCzAJBgNVBAYTAk5aMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A\n" +
            "    MIIBCgKCAQEAkq7TRLYJApohf8EV2rzzB386SdyfaSKIR2wttG7cFuJzPVVuhpVD\n" +
            "    uTcjnilCrvhZe+TRoY3rTphKmevUW7HT/jt/8Q2s3nWSvHW54ArGak6fcQJ9tKhD\n" +
            "    DAdLtQQDWWWbvYyVb06j0Vn74FEhN0nTVNsf1Dt1+YQw6QqCd3nSC3i9zHrFE6N4\n" +
            "    ndlRkg3oDg7bwnnNUnfF+ucfxlUzNcWosFfK9k+d2hKHlp3htDkJPeaw4A/vKGpv\n" +
            "    3bD31XEF46aPttDMl6JWBhD3xQ4k+u8gcauIds5TR/e2FVODJb9vXZDbNTBtLoUI\n" +
            "    KhAFJRmAX15If7VwWh0tVTNQfFy6yi37NwIDAQABo4IBdDCCAXAwDwYDVR0TAQH/\n" +
            "    BAUwAwEB/zCCASwGCWCGSAGG+EIBDQSCAR0TggEZVGhpcyBSb290IGNlcnRpZmlj\n" +
            "    YXRlIHdhcyBnZW5lcmF0ZWQgYnkgQ2hhcmxlcyBQcm94eSBmb3IgU1NMIFByb3h5\n" +
            "    aW5nLiBJZiB0aGlzIGNlcnRpZmljYXRlIGlzIHBhcnQgb2YgYSBjZXJ0aWZpY2F0\n" +
            "    ZSBjaGFpbiwgdGhpcyBtZWFucyB0aGF0IHlvdSdyZSBicm93c2luZyB0aHJvdWdo\n" +
            "    IENoYXJsZXMgUHJveHkgd2l0aCBTU0wgUHJveHlpbmcgZW5hYmxlZCBmb3IgdGhp\n" +
            "    cyB3ZWJzaXRlLiBQbGVhc2Ugc2VlIGh0dHA6Ly9jaGFybGVzcHJveHkuY29tL3Nz\n" +
            "    bCBmb3IgbW9yZSBpbmZvcm1hdGlvbi4wDgYDVR0PAQH/BAQDAgIEMB0GA1UdDgQW\n" +
            "    BBT4kI1UFdFUM/kdjZPIwd8FkJM8VTANBgkqhkiG9w0BAQsFAAOCAQEAiT8Qrc+0\n" +
            "    6mThIIIsCCYCheR/CJmkrmXomQyMpkgp26l8WNhQQiYd3az1mnuo7tdLr+RAiO73\n" +
            "    MmHDyMxzejpOcoCnB8E8en3lfrBppfkZgjf4ux0B4Rfijnm9zUJ9eipi6j7bhwlK\n" +
            "    JR9J5AcvKxmSe+txQlMkpnHFzcRygcvQWLRstQw5ILpZCT8CZ4R1MV6dj2CDdjWL\n" +
            "    xQUEvSb7ib/s8TmlD9Hd6oOZY7KGZc/Qwk9lx+Sy0RHBrju996K64zY58kxBE/d7\n" +
            "    pjx1jNRfc/fx/Bexpoh2dzM/ZO8fud+Q60jF2h86wvtr5kY5+qyzW0H/ySIBWd/M\n" +
            "    WVapl6jydlK+4A==\n" +
            "    -----END CERTIFICATE-----"


    private val retrofitClient: Retrofit
        by lazy {
            val sslSocketFactory = createSSLSocketFactory()

            val httpClient = OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS)
                .apply {
                    sslSocketFactory?.let {
                        sslSocketFactory(sslSocketFactory, TrustAllManager)
                    }
                    addInterceptor(HeaderTokenInterceptor())
                    addInterceptor(TokenExpiredInterceptor())
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
                .retryOnConnectionFailure(true)
                .build()

            val baseUrl = if (ModularContractSDK.host.isEmpty() || (!ModularContractSDK.host.startsWith("http"))){
                SPUtils.getContractBaseUrl()
            } else {
                SPUtils.saveContractBaseUrl(ModularContractSDK.host)
                ModularContractSDK.host
            }

            Retrofit.Builder().client(httpClient).baseUrl(baseUrl).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
        }


    fun <T> create(service: Class<T>): T {
        return retrofitClient.create(service)
    }

    fun getCertificates(vararg certificates: InputStream?): SSLSocketFactory? {
        try {
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = Integer.toString(index++)
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate))
                try {
                    if (certificate != null) certificate.close()
                } catch (e: IOException) {
                }
            }
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            sslContext.init(
                null,
                trustManagerFactory.getTrustManagers(),
                SecureRandom()
            )
            return sslContext.socketFactory

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun createSSLSocketFactory(): SSLSocketFactory? {
        var ssfFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(TrustAllManager), SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (ignored: java.lang.Exception) {
            ignored.printStackTrace()
        }
        return ssfFactory
    }


    object TrustAllManager : X509TrustManager {

        private val _AcceptedIssuers =
            arrayOf<X509Certificate>()

        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            x509Certificates: Array<X509Certificate>, s: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            x509Certificates: Array<X509Certificate>, s: String
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return _AcceptedIssuers
        }

    }

    fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { s, sslSession -> true }
    }

}