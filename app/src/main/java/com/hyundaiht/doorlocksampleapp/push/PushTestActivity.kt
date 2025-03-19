package com.hyundaiht.doorlocksampleapp.push

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.hyundaiht.doorlocksampleapp.JsonFileUtil
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushTestActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private lateinit var db1: AppLocalDatabase1
    private lateinit var db2: AppLocalDatabase2
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db1 = AppLocalDatabase1.getInstance(this@PushTestActivity)
        db2 = AppLocalDatabase2.getInstance(this@PushTestActivity)

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
                        TextWithButton("Push Json File - 삽입") {
                            scope.launch {
                                val pushList = JsonFileUtil.parseJsonFileToList<PushEntity>(
                                    this@PushTestActivity,
                                    "example.json",
                                    gson
                                )
                                Log.d(tag, "Push parseJsonFile pushList = $pushList")

                                db1.pushDao().insertAll(pushList)
                            }
                        }

                        TextWithButton("Push Json String - 삽입") {
                            scope.launch {
                                val jsonString = JsonFileUtil.loadJsonFromAssets(
                                    this@PushTestActivity,
                                    "example.json",
                                ) ?: ""
                                val pushList = JsonFileUtil.parseJsonStringToList<PushEntity>(
                                    jsonString,
                                    gson
                                )
                                Log.d(tag, "Push parseJsonString pushList = $pushList")

                                db1.pushDao().insertAll(pushList)
                            }
                        }

                        TextWithButton("Push Search - 데이터 확인") {
                            scope.launch {
                                val pushList = db1.pushDao().findList("d-000009")
                                Log.d(tag, "Push findList() pushList = $pushList")
                            }
                        }

                        TextWithButton("Push Json File - 데이터 확인") {
                            scope.launch {
                                val pushList = db1.pushDao().allList()
                                Log.d(tag, "Push ushDao allList = $pushList")
                            }
                        }

                        TextWithButton("Push - 데이터 제거") {
                            scope.launch {
                                db1.pushDao().deleteAllList()
                            }
                        }

                        HorizontalDivider(thickness = 30.dp)

                        TextWithButton("DeviceEvent Json File - 삽입") {
                            scope.launch {
                                val pushList = JsonFileUtil.parseJsonFileToList<PushFile>(
                                    this@PushTestActivity, "dummy_data.json"
                                )
                                Log.d(tag, "DeviceEvent parseJsonFile pushList = $pushList")

                                pushList.forEach {
                                    val deviceEvent = gson.fromJson(it.deviceEvent, DeviceEventEntity::class.java)
                                    db2.deviceEventDao().insert(deviceEvent)
                                }
                            }
                        }

                        TextWithButton("DeviceEvent Search - 데이터 확인") {
                            scope.launch {
                                val pushList = db2.deviceEventDao()
                                    .findList("000002ed14fb3239896c482a8ee094c1")
                                Log.d(tag, "DeviceEvent findList() pushList = $pushList")
                            }
                        }

                        TextWithButton("DeviceEvent Json File - 데이터 확인") {
                            scope.launch {
                                val pushList = db2.deviceEventDao().allList()
                                Log.d(tag, "DeviceEvent allList = $pushList")
                            }
                        }

                        TextWithButton("DeviceEvent - 데이터 제거") {
                            scope.launch {
                                db2.deviceEventDao().deleteAllList()
                            }
                        }
                    }
                }
            }
        }
    }


}