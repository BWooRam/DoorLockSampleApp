package com.hyundaiht.doorlocksampleapp.websocket

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.hyundaiht.doorlocksampleapp.api.ApiModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebSocketService : Service() {
    private val tag = javaClass.simpleName
    private val binder = LocalBinder(tag)

    class LocalBinder(
        private val tag: String
    ) : Binder(), WebSocketBinder {
        private val appWebSocketClient = AppSocketClient(ApiModule.TEST_APP_ID)
        private val ioScope = CoroutineScope(Dispatchers.IO)

        override fun addListener(id: Int, listener: WebSocketListener) {
            appWebSocketClient.addListener(id, listener)
        }

        override fun removeListener(id: Int) {
            appWebSocketClient.removeListener(id)
        }

        override fun openWebSocket() {
            ioScope.launch {
                Log.d(tag, "openWebSocket start")
                appWebSocketClient.openWebSocket()
            }
        }

        override fun closeWebSocket() {
            ioScope.launch {
                Log.d(tag, "closeWebSocket start")
                appWebSocketClient.closeWebSocket()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(tag, "onBind start")
        binder.openWebSocket()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate start")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d(tag, "onStart start")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(tag, "onRebind start")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand start")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy start")
        binder.closeWebSocket()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(tag, "onTaskRemoved start")
        binder.closeWebSocket()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "onUnbind start")
        return true
    }

    companion object {
        fun binding(context: Context, connection: ServiceConnection) {
            context.bindService(
                Intent(context, WebSocketService::class.java), connection, Context.BIND_AUTO_CREATE
            )
        }

        fun unBinding(context: Context, connection: ServiceConnection) {
            context.unbindService(connection)
        }
    }
}