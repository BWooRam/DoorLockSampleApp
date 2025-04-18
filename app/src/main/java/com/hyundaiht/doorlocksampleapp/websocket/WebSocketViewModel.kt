package com.hyundaiht.doorlocksampleapp.websocket

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hyundaiht.doorlocksampleapp.AppApplication
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class WebSocketViewModel : ViewModel() {
    private val tag = javaClass.simpleName
    private val binder = WebSocketService.LocalBinder(tag)

    fun openWebSocket() {
        binder.openWebSocket()
        binder.addListener(hashCode(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "WebSocket open")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(tag, "Receiving : $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(tag, "Receiving : $bytes")
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.d(tag, "Closing : $code / $reason")
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.d(tag, "onClosed : $code / $reason")
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                Log.d(tag, "onFailure : " + t.message)
            }
        })
    }

    fun closeWebSocket() {
        binder.removeListener(hashCode())
        binder.closeWebSocket()
    }
}