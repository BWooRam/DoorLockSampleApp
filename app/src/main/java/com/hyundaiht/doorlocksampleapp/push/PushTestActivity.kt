package com.hyundaiht.doorlocksampleapp.push

import android.content.Context
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
import com.google.gson.stream.JsonReader
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader

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
                                val pushList = parseJsonFile<PushEntity>(
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
                                val jsonString = loadJsonFromAssets(
                                    this@PushTestActivity,
                                    "example.json",
                                ) ?: ""
                                val pushList = parseJsonString<PushEntity>(
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
                                val pushList = parseJsonFile<PushFile>(
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

    private fun loadJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun loadLargeJsonFromAssets(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line) // 한 줄씩 읽어서 추가
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    private inline fun <reified T> parseJsonFile(
        context: Context, fileName: String, gson: Gson = Gson()
    ): List<T> {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
            jsonStringToClass(reader, gson)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private inline fun <reified T> parseJsonString(
        jsonString: String, gson: Gson = Gson()
    ): List<T> {
        return try {
            val reader = JsonReader(StringReader(jsonString))
            jsonStringToClass(reader, gson)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private inline fun <reified T> jsonStringToClass(
        reader: JsonReader, gson: Gson = Gson()
    ): List<T> {
        val dataList = mutableListOf<T>()
        try {
            reader.beginArray() // 🔹 JSON 배열 시작 `[` 인식

            while (reader.hasNext()) {
                val data: T = gson.fromJson(reader, T::class.java) // 🔹 Object 단위로 변환
                dataList.add(data) // 🔹 리스트에 추가
            }

            reader.endArray() // 🔹 JSON 배열 끝 `]` 인식
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dataList
    }
}