package com.hyundaiht.doorlocksampleapp.websocket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.hyundaiht.doorlocksampleapp.AppApplication
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class WebSocketTestApplicationActivity : FragmentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TextWithButton("Application WebSocket - addListener() 실행") {
                            initBinderListener()
                        }

                        TextWithButton("Application WebSocket - removeListener() 실행") {
                            removeBinderListener()
                        }

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart start")
        initBinderListener()
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop start")
        removeBinderListener()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initBinderListener() {
        val app = application as? AppApplication
            ?: throw NullPointerException("AppApplication is null")

        app.binder.openWebSocket()
        app.binder.addListener(hashCode(), object : WebSocketListener() {
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
                super.onClosed(webSocket, code, reason)
                Log.d(tag, "onClosed : $code / $reason")
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
                Log.d(tag, "onFailure : " + t.message)
            }
        })
    }

    private fun removeBinderListener() {
        val app = application as? AppApplication
            ?: throw NullPointerException("AppApplication is null")

        app.binder.removeListener(hashCode())
        app.binder.closeWebSocket()
    }

}
