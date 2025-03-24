package com.hyundaiht.doorlocksampleapp.property

import RuntimeTypeAdapterFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.gson.GsonBuilder
import com.hyundaiht.doorlocksampleapp.JsonFileUtil
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PropertyTestActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private val isText = mutableStateOf<Boolean>(false)
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val gson = GsonBuilder().registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory.of(Configuration::class.java, "type")
            .registerSubtype(Configuration.MultiOfArray::class.java, "multiOfArray")
            .registerSubtype(Configuration.OneOfArray::class.java, "oneOfArray")
            .registerSubtype(Configuration.OneOfRange::class.java, "oneOfRange")
            .registerSubtype(Configuration.SingleValue::class.java, "singleValue")
            .registerSubtype(Configuration.RecordSet::class.java, "recordSet")
    ).create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val scrollState = rememberScrollState()
                    var property by remember { mutableStateOf<DeviceProperty?>(null) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                    ) {
                        TextWithButton("Device Property - 모드 변경 : ${if (isText.value) "Text" else "UI"}") {
                            ioScope.launch {
                                isText.value = !isText.value
                            }
                        }

                        TextWithButton("Device Property - Json File Parsing") {
                            ioScope.launch {
                                val jsonString = JsonFileUtil.loadLargeJsonFromAssets(
                                    this@PropertyTestActivity, "property_sample.json"
                                )

                                property = JsonFileUtil.parseJsonString<DeviceProperty>(
                                    jsonString, gson
                                )
                            }
                        }

                        if (property != null) {
                            property?.states?.forEach {
                                HorizontalDivider(thickness = 30.dp, color = Color.White)
                                PropertyState(it)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PropertyState(states: States) {
        val isText by remember { isText }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Cyan),
                text = states.text
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Green),
                text = states.description
            )
            when (states.configuration) {
                is Configuration.MultiOfArray -> {
                    PropertyState_MultiOfArray(states.configuration, isText)
                }

                is Configuration.OneOfArray -> {
                    PropertyState_OneOfArray(states.configuration, isText)
                }

                is Configuration.OneOfRange -> {
                    Log.d(tag, "Configuration.OneOfRange configuration  = ${states.configuration}")
                    PropertyState_OneOfRange(states.configuration, isText)
                }

                is Configuration.SingleValue -> {
                    PropertyState_SingleValue(states.configuration, isText)
                }

                is Configuration.RecordSet -> {
                    Log.d(tag, "Configuration.RecordSet configuration  = ${states.configuration}")
                    PropertyState_RecordSet(states.configuration, isText)
                }
            }
        }
    }

    @Composable
    fun PropertyState_SingleValue(configuration: Configuration.SingleValue, isText: Boolean) {
        if (isText) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Yellow)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "type = ${configuration.type}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "dataType = ${configuration.dataType}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "min = ${configuration.min}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "max = ${configuration.max}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "editable = ${configuration.editable}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "digitOnly = ${configuration.digitOnly}"
                )
            }
        } else {
            val min = configuration.min ?: 0
            val max = configuration.max ?: Int.MAX_VALUE
            val dataType = SingleValueDataType.convertString(configuration.dataType)
            val editable = configuration.editable

            LimitedTypedTextField(
                minLength = min, maxLength = max, dataType, editable
            ) {
                Toast.makeText(
                    this@PropertyTestActivity, "입력 데이터 : $it", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Composable
    fun PropertyState_OneOfRange(configuration: Configuration.OneOfRange, isText: Boolean) {
        if (isText) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Yellow)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "type = ${configuration.type}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "dataType = ${configuration.dataType}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "min = ${configuration.min}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "max = ${configuration.max}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "step = ${configuration.step}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "unit = ${configuration.unit}"

                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "editable = ${configuration.editable}"
                )
            }
        } else {
            val min = configuration.min.toFloat()
            val max = configuration.max.toFloat()
            val step = configuration.step.toFloat()
            val unit = configuration.unit ?: ""
            val isEnable = configuration.editable

            val steps = getUiSteps(min, max, step)
            if (steps > 50) {
                BasicSlider(
                    min = min, max = max, step = step, unit = unit, isEnable = isEnable
                )
            } else {
                BoundedCounterUI(
                    min = min, max = max, step = step, unit = unit, isEnable = isEnable
                )
            }
        }
    }

    @Composable
    fun PropertyState_OneOfArray(
        configuration: Configuration.OneOfArray, isText: Boolean
    ) {
        if (isText) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Yellow)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "type = ${configuration.type}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "dataType = ${configuration.dataType}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "readable = ${configuration.readable}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "writable = ${configuration.writable}"
                )
            }
        } else {
            var uiType by remember { mutableStateOf(false) }

            TextWithButton("Ui Type 변경") {
                ioScope.launch {
                    uiType = !uiType
                }
            }
            when {
                configuration.writable.isNotEmpty() -> if (uiType) {
                    ExposedDropdownMenus(
                        configuration.writable, true
                    )
                } else {
                    ChipFlowRow(
                        configuration.writable, 1, true
                    )
                }

                configuration.readable.isNotEmpty() -> if (uiType) {
                    ExposedDropdownMenus(
                        configuration.readable, false
                    )
                } else {
                    ChipFlowRow(
                        configuration.readable, 1, false
                    )
                }
            }
        }
    }

    @Composable
    fun PropertyState_MultiOfArray(
        configuration: Configuration.MultiOfArray, isText: Boolean
    ) {
        if (isText) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Yellow)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "type = ${configuration.type}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "dataType = ${configuration.dataType}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "readable = ${configuration.readable}"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Yellow),
                    text = "writable = ${configuration.writable}"
                )
            }
        } else {
            var uiType by remember { mutableStateOf(false) }

            TextWithButton("Ui Type 변경") {
                ioScope.launch {
                    uiType = !uiType
                }
            }

            val limit = configuration.limit ?: throw NullPointerException()
            when {
                configuration.writable.isNotEmpty() -> if (uiType) {
                    BasicCheckBoxes(
                        configuration.writable, limit, true
                    )
                } else {
                    ChipFlowRow(
                        configuration.writable, limit, true
                    )
                }

                configuration.readable.isNotEmpty() -> if (uiType) {
                    BasicCheckBoxes(
                        configuration.writable, limit, true
                    )
                } else {
                    ChipFlowRow(
                        configuration.writable, limit, true
                    )
                }
            }
        }
    }

    @Composable
    fun PropertyState_RecordSet(
        configuration: Configuration.RecordSet, isText: Boolean
    ) {
        if (isText) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Yellow)
            ) {
                configuration.record.forEach { record ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Yellow), text = "id = ${record.id}"
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Yellow), text = "text = ${record.text}"
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Yellow), text = "limit = ${record.limit}"
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(Color.Yellow),
                        text = "recordItemConfiguration = ${record.configuration}"
                    )

                    HorizontalDivider(thickness = 10.dp, color = Color.Gray)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                configuration.record.forEach { record ->
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    Text("id = ${record.id}, text = ${record.text}")
                    when (val recordItemConfiguration = record.configuration) {
                        is Configuration.MultiOfArray -> {
                            PropertyState_MultiOfArray(recordItemConfiguration, isText)
                        }

                        is Configuration.OneOfArray -> {
                            PropertyState_OneOfArray(recordItemConfiguration, isText)
                        }

                        is Configuration.OneOfRange -> {
                            PropertyState_OneOfRange(recordItemConfiguration, isText)
                        }

                        is Configuration.SingleValue -> {
                            PropertyState_SingleValue(recordItemConfiguration, isText)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

}