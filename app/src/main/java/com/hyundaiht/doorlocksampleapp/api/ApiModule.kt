package com.hyundaiht.doorlocksampleapp.api

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiModule {
    private val tag = javaClass.simpleName
    const val ACCESS_TOKEN = "A6AbGpjBc5XwrKU3icMG5"
    const val HT_IOT_APE_URL1 = "https://177b5a76-7318-4776-b141-2cf2ac75af76.mock.pstmn.io"
    const val HT_IOT_APE_URL2 = "https://htiot-dev-api-frontend.htiotservice.com"
    const val USER_LOGIN_URL = "https://hthome-auth-develop.htiotservice.com"
    const val TEST_APP_ID = "11f015a6f7186232abb99901c0905b2c"

    val client = OkHttpClient.Builder().addInterceptor(AuthInterceptor(ACCESS_TOKEN))
        .addNetworkInterceptor(providesLoggingInterceptor())
        .sslSocketFactory(createUnsafeSslSocketFactory(), createUnsafeTrustManager())
        .hostnameVerifier { _, _ -> true }  // 모든 호스트명 확인을 무시
        .build()

    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    fun provideHtHomeAfeApi(): HtHomeAfeApi {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(HT_IOT_APE_URL2)
            .client(client)
            .build()

        return retrofit.create(HtHomeAfeApi::class.java)
    }

    // SSL을 비활성화하는 메서드들
    fun createUnsafeSslSocketFactory(): SSLSocketFactory {
        val trustAllCertificates = object : X509TrustManager {
            @SuppressLint("CustomX509TrustManager")
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(tag, "createUnsafeSslSocketFactory checkClientTrusted authType = $authType, chain = $chain")
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(tag, "createUnsafeSslSocketFactory checkServerTrusted authType = $authType, chain = $chain")
                try {
                    // Perform server certificate chain validation
                    for (certificate in chain.orEmpty()) {
                        certificate.checkValidity()
                        Log.d(tag, "createUnsafeSslSocketFactory chain certificate = $certificate")
                    }
                } catch (e: CertificateException) {
                    Log.d(tag, "createUnsafeSslSocketFactory chain error = $e")
                    throw CertificateException("SSL certificate validation error: ${e.message}")
                }
            }
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustAllCertificates), SecureRandom())

        return sslContext.socketFactory
    }

    @SuppressLint("CustomX509TrustManager")
    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(tag, "createUnsafeTrustManager checkClientTrusted authType = $authType, chain = $chain")
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                Log.d(tag, "createUnsafeTrustManager checkServerTrusted authType = $authType, chain = $chain")
                try {
                    // Perform server certificate chain validation
                    for (certificate in chain.orEmpty()) {
                        certificate.checkValidity()
                        Log.d(tag, "createUnsafeTrustManager chain certificate = $certificate")
                    }
                } catch (e: CertificateException) {
                    Log.d(tag, "createUnsafeTrustManager chain error = $e")
                    throw CertificateException("SSL certificate validation error: ${e.message}")
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }

}
