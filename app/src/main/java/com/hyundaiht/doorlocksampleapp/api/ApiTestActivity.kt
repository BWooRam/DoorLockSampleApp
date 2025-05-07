package com.hyundaiht.doorlocksampleapp.api

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ApiTestActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val htHomeAfeApi = ApiModule.provideHtHomeAfeApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                    ) {
                        TextWithButton("HT Home API 테스트 - getDeviceList()") {
                            scope.launch {
                                val result = htHomeAfeApi.getDeviceList().body()
                                Log.d(tag, "getDeviceList result = $result")
                            }
                        }

                        TextWithButton("HT Home API 테스트 - controlDevice()") {
                            scope.launch {
                                val jsonBody = "{\n" +
                                        "    \"states\": {\n" +
                                        "        \"pinKey\": {\n" +
                                        "            \"nickname\": \"일반 비밀번호\",\n" +
                                        "            \"password\": \"0123456789\"\n" +
                                        "        }\n" +
                                        "    }\n" +
                                        "}"

                                val requestBody =
                                    jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
                                val result = htHomeAfeApi.controlDevice(
                                    deviceId = ApiModule.TEST_APP_ID,
                                    requestBody = requestBody
                                )
                                Log.d(tag, "controlDevice result = $result")
                            }
                        }

                        TextWithButton("HT Home API 테스트 - getSubscribeDevice()") {
                            scope.launch {
                                val result = htHomeAfeApi.getSubscribeDevice(
                                    deviceId = ApiModule.TEST_APP_ID
                                )
                                Log.d(tag, "getSubscribeDevice result = $result")
                            }
                        }

                        TextWithButton("HT Home API 테스트 - subscribeDevice()") {
                            scope.launch {
                                val jsonBody = "{\n" +
                                        "    \"deviceIds\": [\n" +
                                        "        \"${ApiModule.TEST_APP_ID}\"\n" +
                                        "    ]\n" +
                                        "}"

                                val result = htHomeAfeApi.subscribeDevice(
                                    deviceId = ApiModule.TEST_APP_ID,
                                    requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
                                )
                                Log.d(tag, "subscribeDevice result = $result")
                            }
                        }

                        TextWithButton("HT Home API 테스트 - unsubscribeDevice()") {
                            scope.launch {
                                val jsonBody = "{\n" +
                                        "    \"deviceIds\": [\n" +
                                        "        \"${ApiModule.TEST_APP_ID}\"\n" +
                                        "    ]\n" +
                                        "}"

                                val result = htHomeAfeApi.unsubscribeDevice(
                                    deviceId = ApiModule.TEST_APP_ID,
                                    okHttpClient = ApiModule.client,
                                    jsonBody = jsonBody
                                ).execute()
                                Log.d(tag, "unsubscribeDevice result = $result")
                            }
                        }
                    }
                }
            }
        }
    }
}