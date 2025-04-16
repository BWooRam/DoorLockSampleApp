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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hyundaiht.doorlocksampleapp.AppApplication
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class WebSocketTestViewModelActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private val gson = Gson()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var viewModel: WebSocketViewModel? = null

    inner class WebSocketViewModelFactory(
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WebSocketViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WebSocketViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        viewModel = ViewModelProvider(this, WebSocketViewModelFactory())[WebSocketViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TextWithButton("ViewModel WebSocket - openWebSocket() 실행") {
                            viewModel?.openWebSocket()
                        }

                        TextWithButton("ViewModel WebSocket - closeWebSocket() 실행") {
                            viewModel?.closeWebSocket()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart start")
        viewModel?.openWebSocket()
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop start")
        viewModel?.closeWebSocket()
    }
}
