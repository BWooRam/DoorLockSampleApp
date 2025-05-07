package com.hyundaiht.doorlocksampleapp.api

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.api.ApiModule
import com.hyundaiht.doorlocksampleapp.property.Configuration
import com.hyundaiht.doorlocksampleapp.property.Device
import com.hyundaiht.doorlocksampleapp.property.DeviceDetails
import com.hyundaiht.doorlocksampleapp.property.DevicePropertyResponse
import com.hyundaiht.doorlocksampleapp.property.DeviceResponse
import com.hyundaiht.doorlocksampleapp.property.PropertyState
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import com.hyundaiht.doorlocksampleapp.websocket.WebSocketListener
import com.hyundaiht.doorlocksampleapp.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

class ApiSampleActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private val gson = GsonBuilder().registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory.of(Configuration::class.java, "type")
            .registerSubtype(Configuration.MultiOfArray::class.java, "multiOfArray")
            .registerSubtype(Configuration.OneOfArray::class.java, "oneOfArray")
            .registerSubtype(Configuration.OneOfRange::class.java, "oneOfRange")
            .registerSubtype(Configuration.SingleValue::class.java, "singleValue")
            .registerSubtype(Configuration.RecordSet::class.java, "recordSet")
    ).create()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val htHomeAfeApi = ApiModule.provideHtHomeAfeApi()
    private val isText = mutableStateOf(false)
    private val devicePropertyResponse = mutableStateOf<DeviceDetails?>(null)
    private val deviceResponse = mutableStateOf<DeviceResponse?>(null)
    private var localBinder: WebSocketService.LocalBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(tag, "onServiceConnected className = $className")
            localBinder = service as? WebSocketService.LocalBinder
            localBinder?.addListener(hashCode(), object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d(tag, "WebSocket open")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d(tag, "Receiving : $text")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    Log.d(tag, "Receiving : $bytes")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(tag, "Closing : $code / $reason")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(tag, "onClosed : $code / $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.d(tag, "onFailure : " + t.message)
                }
            })
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d(tag, "onServiceDisconnected className = $className")
            localBinder?.removeListener(hashCode())
            localBinder = null
        }

        override fun onBindingDied(className: ComponentName?) {
            Log.d(tag, "onBindingDied className = $className")
            localBinder?.removeListener(hashCode())
            localBinder = null
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        WebSocketService.Companion.binding(this@ApiSampleActivity, connection)
//        startService(Intent(this@SampleActivity, WebSocketService::class.java))
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var rememberStates by remember { devicePropertyResponse }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TextWithButton("뒤로가기") {
                            rememberStates = null
                        }

                        if (rememberStates != null) {
                            val property = rememberStates?.properties ?: throw NullPointerException(
                                "property is NULL"
                            )
                            LazyColumn {
                                items(property.states.size){
                                    val target = property.states[it]
                                    PropertyState(target, isText)
                                }
                            }
                        } else DeviceList()
                    }
                }
            }
        }
    }

    @Composable
    fun DeviceList() {
        var rememberDeviceList by remember { deviceResponse }

        TextWithButton("DeviceList 가져오기") {
            ioScope.launch {
                val result = htHomeAfeApi.getDeviceList()
                Log.d(tag, "DeviceList result = $result")
                rememberDeviceList = result.body()
            }
        }
        LazyColumn {
            items(rememberDeviceList?.resultData?.size ?: 0) { item ->
                val device = rememberDeviceList?.resultData?.get(item) ?: return@items
                DeviceItem(device)
            }
        }
    }

    @Composable
    fun DeviceItem(device: Device) {
        val deviceTitle = device.deviceId
        val deviceContent = "${device.deviceNickname ?: "Unknown"}\n${device.deviceType}"
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                ioScope.launch {
                    val result = htHomeAfeApi.getDeviceProperty(device.deviceId).body() ?: return@launch
                    Log.d(tag, "DeviceList result = $result")
                    devicePropertyResponse.value = gson.fromJson(result.resultData, DeviceDetails::class.java)
                    Log.d(tag, "DeviceList devicePropertyResponse = $devicePropertyResponse")
                }
            }) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), text = deviceTitle
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), text = deviceContent
            )
            HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
        WebSocketService.Companion.unBinding(this@ApiSampleActivity, connection)
    }

}
