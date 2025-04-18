package com.hyundaiht.doorlocksampleapp.websocket

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
import com.hyundaiht.doorlocksampleapp.AppApplication
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.api.ApiModule
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class WebSocketTestKtorActivity : FragmentActivity() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val socketClientKtor = AppSocketClientKtor(ApiModule.TEST_APP_ID)
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
                            openWebSocket()
                        }

                        TextWithButton("Application WebSocket - removeListener() 실행") {
                            closeWebSocket()
                        }

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart start")
        openWebSocket()
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop start")
        closeWebSocket()
    }

    private fun openWebSocket() {
        scope.launch {
            socketClientKtor.openWebSocket()
        }
    }

    private fun closeWebSocket() {
        scope.launch {
            socketClientKtor.closeWebSocket()
        }
    }

}
