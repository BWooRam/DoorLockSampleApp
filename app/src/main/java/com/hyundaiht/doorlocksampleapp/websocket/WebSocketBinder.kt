package com.hyundaiht.doorlocksampleapp.websocket

import android.util.Log
import kotlinx.coroutines.launch

interface WebSocketBinder {
    fun addListener(id: Int, listener: WebSocketListener)
    fun removeListener(id: Int)
    fun openWebSocket()
    fun closeWebSocket()
}