package com.hyundaiht.doorlocksampleapp.websocket

import android.util.Log
import com.hyundaiht.doorlocksampleapp.api.ApiModule
import com.hyundaiht.doorlocksampleapp.api.ApiModule.providesLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class AppSocketClient(
    private val deviceID: String
) {
    private val listenerHashMap: MutableMap<Int, WebSocketListener> = mutableMapOf()
    private val tag = javaClass.simpleName
    private val client =
        OkHttpClient.Builder().addNetworkInterceptor(providesLoggingInterceptor()).build()

    private val request = Request.Builder()
        .url("ws://15.164.79.125:9081/devices/${deviceID}?accessToken=${ApiModule.ACCESS_TOKEN}")
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
    fun addListener(id: Int, listener: WebSocketListener) {
        listenerHashMap[id] = listener
    }

    /**
     * TODO
     *
     * @param id
     */
    fun removeListener(id: Int) {
        listenerHashMap.remove(id)
    }

    /**
     * TODO
     *
     */
    fun clearListener() {
        listenerHashMap.clear()
    }

    fun openWebSocket() {
        if (webSocket != null) {
            Log.i("SampleActivity", "It's already start WebSocket")
            return
        }

        webSocket = client.newWebSocket(
            request, listener
        )
    }

    fun closeWebSocket() {
        if (webSocket == null) {
            Log.i("SampleActivity", "It's already close WebSocket")
            return
        }

        webSocket?.close(
            WebSocketListener.NORMAL_CLOSURE_STATUS, null
        )
        webSocket = null
    }

    fun sendData(text: String): Boolean = webSocket?.send(text) ?: false
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