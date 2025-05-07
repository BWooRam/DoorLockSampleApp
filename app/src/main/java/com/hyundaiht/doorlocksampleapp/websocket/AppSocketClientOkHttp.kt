package com.hyundaiht.doorlocksampleapp.websocket

import android.util.Log
import com.hyundaiht.doorlocksampleapp.api.ApiModule
import com.hyundaiht.doorlocksampleapp.api.ApiModule.providesLoggingInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import io.ktor.websocket.send
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Scanner
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface WebSocketClient {
    val listenerHashMap: MutableMap<Int, WebSocketListener>
    fun addListener(id: Int, listener: WebSocketListener)
    fun removeListener(id: Int)
    fun clearListener()
    suspend fun openWebSocket()
    suspend fun closeWebSocket()
    suspend fun sendData(text: String): Boolean
}

const val DCH_IP = "htiot-dev-device-c-holder.htiotservice.com:443"

class AppSocketClientOkHttp(
    private val deviceID: String
) : WebSocketClient {
    override val listenerHashMap: MutableMap<Int, WebSocketListener> = mutableMapOf()
    private val tag = javaClass.simpleName

    //    private val client = OkHttpClient.Builder().addNetworkInterceptor(providesLoggingInterceptor()).build()
    private val client = getUnsafeOkHttpClient()

    fun getUnsafeOkHttpClient(): OkHttpClient {
        // 모든 인증서를 신뢰하는 TrustManager
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
                Log.d(tag, "getUnsafeOkHttpClient checkClientTrusted authType = $authType, chain = $chain")
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                Log.d(tag, "getUnsafeOkHttpClient checkServerTrusted authType = $authType, chain = $chain")
                try {
                    // Perform server certificate chain validation
                    for (certificate in chain.orEmpty()) {
                        certificate.checkValidity()
                        Log.d(tag, "chain certificate = $certificate")
                    }
                } catch (e: CertificateException) {
                    Log.d(tag, "chain error = $e")
                    throw CertificateException("SSL certificate validation error: ${e.message}")
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // 호스트 이름 무시
            .addNetworkInterceptor(providesLoggingInterceptor())
            .build()
    }

    private val request = Request.Builder()
        .url("wss://$DCH_IP/devices/${deviceID}?accessToken=${ApiModule.ACCESS_TOKEN}")
        .build()

    private val listener: WebSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(tag, "WebSocket open")
            listenerHashMap.forEach { (_, listener) ->
                listener.onOpen(webSocket, response)
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(tag, "Receiving : $text")
            listenerHashMap.forEach { (_, listener) ->
                listener.onMessage(webSocket, text)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(tag, "Receiving : $bytes")
            listenerHashMap.forEach { (_, listener) ->
                listener.onMessage(webSocket, bytes)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(tag, "Closing : $code / $reason")
            listenerHashMap.forEach { (_, listener) ->
                listener.onClosing(webSocket, code, reason)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(tag, "onClosed : $code / $reason")
            listenerHashMap.forEach { (_, listener) ->
                listener.onClosed(webSocket, code, reason)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(tag, "onFailure : " + t.message)
            listenerHashMap.forEach { (_, listener) ->
                listener.onFailure(webSocket, t, response)
            }
        }

    }
    private var webSocket: WebSocket? = null

    /**
     * TODO
     *
     * @param id
     * @param listener
     */
    override fun addListener(id: Int, listener: WebSocketListener) {
        listenerHashMap[id] = listener
    }

    /**
     * TODO
     *
     * @param id
     */
    override fun removeListener(id: Int) {
        listenerHashMap.remove(id)
    }

    /**
     * TODO
     *
     */
    override fun clearListener() {
        listenerHashMap.clear()
    }

    override suspend fun openWebSocket() {
        if (webSocket != null) {
            Log.i("SampleActivity", "It's already start WebSocket")
            return
        }

        webSocket = client.newWebSocket(
            request, listener
        )
    }

    override suspend fun closeWebSocket() {
        if (webSocket == null) {
            Log.i("SampleActivity", "It's already close WebSocket")
            return
        }

        webSocket?.close(
            WebSocketListener.NORMAL_CLOSURE_STATUS, null
        )
        webSocket = null
    }

    override suspend fun sendData(text: String): Boolean = webSocket?.send(text) ?: false
}

open class WebSocketListener : okhttp3.WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("Socket", "WebSocket open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d("Socket", "Receiving : $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d("Socket", "Receiving : $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Socket", "Closing : $code / $reason")
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("Socket", "Error : " + t.message)
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

}


class AppSocketClientKtor(
    private val deviceID: String
) : WebSocketClient {
    override val listenerHashMap: MutableMap<Int, WebSocketListener> = mutableMapOf()
    private val tag = javaClass.simpleName
    private val client = HttpClient(CIO) {
        install(WebSockets) {
//            maxFrameSize
//            contentConverter
//            pingInterval
//            pingIntervalMillis
        }
        engine {
            requestTimeout = 15_000
            endpoint {
                connectTimeout = 15_000
                keepAliveTime = 5000
            }
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    /**
     * TODO
     *
     * @param id
     * @param listener
     */
    override fun addListener(id: Int, listener: WebSocketListener) {
        listenerHashMap[id] = listener
    }

    /**
     * TODO
     *
     * @param id
     */
    override fun removeListener(id: Int) {
        listenerHashMap.remove(id)
    }

    /**
     * TODO
     *
     */
    override fun clearListener() {
        listenerHashMap.clear()
    }

    override suspend fun openWebSocket() {
        runCatching {
            client.webSocket("ws://15.164.79.125:9081/devices/${deviceID}?accessToken=${ApiModule.ACCESS_TOKEN}") {
                session = this
                while (true) {
                    when (val othersMessage = incoming.receive()) {
                        is Frame.Text -> {
                            Log.d(
                                tag,
                                "Frame.Text type = ${othersMessage.frameType}, data = ${othersMessage.readText()}"
                            )
                        }

                        is Frame.Binary -> {
                            Log.d(
                                tag,
                                "Frame.Binary type = ${othersMessage.frameType}, data = ${othersMessage.readBytes()}"
                            )
                        }

                        is Frame.Close -> {
                            Log.d(
                                tag,
                                "Frame.Close type = ${othersMessage.frameType}, data = ${othersMessage.readReason()}"
                            )
                        }

                        is Frame.Ping -> {
                            Log.d(
                                tag,
                                "Frame.Ping type = ${othersMessage.frameType}, data = ${othersMessage.readBytes()}"
                            )
                        }

                        is Frame.Pong -> {
                            Log.d(
                                tag,
                                "Frame.Pong type = ${othersMessage.frameType}, data = ${othersMessage.readBytes()}"
                            )
                        }
                    }
                }
            }
        }.onSuccess {
            Log.d(tag, "openWebSocket onSuccess")
        }.onFailure {
            Log.d(tag, "openWebSocket onFailure error = $it")
        }
    }

    override suspend fun closeWebSocket() {
        runCatching {
            client.close()
        }.onSuccess {
            Log.d(tag, "closeWebSocket onSuccess")
        }.onFailure {
            Log.d(tag, "closeWebSocket onFailure error = $it")
        }
    }

    override suspend fun sendData(text: String): Boolean = kotlin.runCatching {
        session?.send(text)
    }.isSuccess
}