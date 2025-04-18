package com.hyundaiht.doorlocksampleapp

import android.app.Application
import com.hyundaiht.doorlocksampleapp.websocket.WebSocketService

class AppApplication : Application(){
    private val tag = javaClass.simpleName
    val binder = WebSocketService.LocalBinder(tag)
    /*private var localBinder: WebSocketService.LocalBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(tag, "onServiceConnected className = $className")
            localBinder = service as? WebSocketService.LocalBinder
            localBinder?.addListener(hashCode(), object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    Log.d(tag, "WebSocket open")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    Log.d(tag, "Receiving : $text")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    Log.d(tag, "Receiving : $bytes")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(tag, "Closing : $code / $reason")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    Log.d(tag, "onClosed : $code / $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    Log.d(tag, "onFailure : " + t.message)
                }
            })
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d(tag, "onServiceDisconnected className = $className")
            localBinder?.removeListener(hashCode())
            localBinder = null
        }
    }*/

    override fun onCreate() {
        super.onCreate()
//        WebSocketService.binding(this@AppApplication, connection)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    /*fun disconnectWebSocket() {
        WebSocketService.unBinding(this@AppApplication, connection)
    }*/
}