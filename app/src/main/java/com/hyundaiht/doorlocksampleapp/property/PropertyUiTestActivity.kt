package com.hyundaiht.doorlocksampleapp.property

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PropertyUiTestActivity : FragmentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val scrollState = rememberScrollState()
                    var rememberUiType by remember { mutableStateOf(UiType.OneOfArray) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                    ) {
                        TextWithButton("MultiOfArray Screen") {
                            rememberUiType = UiType.MultiOfArray
                        }

                        TextWithButton("OneOfArray Screen") {
                            rememberUiType = UiType.OneOfArray
                        }

                        TextWithButton("OneOfRange Screen") {
                            rememberUiType = UiType.OneOfRange
                        }

                        TextWithButton("SingleValue Screen") {
                            rememberUiType = UiType.SingleValue
                        }

                        TextWithButton("Device Property Sample 이동") {
                            startActivity(
                                Intent(
                                    this@PropertyUiTestActivity, PropertyTestActivity::class.java
                                )
                            )
                        }
                        HorizontalDivider(thickness = 2.dp, color = Color.Black)
                        when (rememberUiType) {
                            UiType.OneOfArray -> OneOfArrayScreen()
                            UiType.MultiOfArray -> MultiOfArrayScreen()
                            UiType.OneOfRange -> OneOfRangeScreen()
                            UiType.SingleValue -> SingleValueScreen()
                        }
                    }
                }
            }
        }
    }

    private enum class UiType {
        OneOfArray, MultiOfArray, OneOfRange, SingleValue
    }

    /**
     * 여러 개의 상태 중 "단일 선택"
     * ✅ 드롭다운 (Dropdown) → 값이 많을 때
     * ✅ 라디오 버튼 (Radio Button) → 값이 적을 때
     *
     */
    @Composable
    fun OneOfArrayScreen() {
        var rememberEnable by remember { mutableStateOf(true) }
        val items = mutableListOf("냉방", "난방", "제습", "송풍", "건조", "자동")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TextWithButton("UI Enable 변경") {
                rememberEnable = !rememberEnable
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("속성 값이 많을때")
            ExposedDropdownMenus(
                items, rememberEnable
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("속성 값이 적을때(UI TYPE1)")
            RadioButtons(
                items, rememberEnable
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("속성 값이 적을때(UI TYPE2)")
            ChipFlowRow(items, 1, rememberEnable)
        }
    }

    /**
     * 여러 개의 상태 중 "다중 선택"
     * ✅ 체크박스 리스트 (Checkbox List) → 여러 개 선택 가능할 때
     * ✅ 카드형 선택 (Card UI)
     *
     */
    @Composable
    fun MultiOfArrayScreen() {
        var rememberEnable by remember { mutableStateOf(true) }
        val items = mutableListOf("냉방", "난방", "제습", "송풍", "건조", "자동")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TextWithButton("UI Enable 변경") {
                rememberEnable = !rememberEnable
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("UI Type1")
            BasicCheckBoxes(
                items, 3, rememberEnable
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("UI Type2")
            ChipFlowRow(
                items, 3, rememberEnable
            )
        }
    }

    @Composable
    fun OneOfRangeScreen() {
        var rememberEnable by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text("steps = ((max - min) / step) - 1")
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            TextWithButton("UI Enable 변경") {
                rememberEnable = !rememberEnable
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("steps가 많을때")
            BasicSlider(
                min = 0f, max = 100f, step = 1f, unit = "%", isEnable = rememberEnable
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("steps가 적을때")
            BoundedCounterUI(
                min = 0f, max = 10f, step = 1f, unit = "%", isEnable = rememberEnable
            )
        }
    }

    @Composable
    fun SingleValueScreen() {
        var rememberEnable by remember { mutableStateOf(true) }
        var rememberType by remember { mutableStateOf(SingleValueDataType.string) }
        val type = mutableListOf(
            SingleValueDataType.string.name,
            SingleValueDataType.int.name,
            SingleValueDataType.long.name,
            SingleValueDataType.timestamp.name
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TextWithButton("UI Enable 변경") {
                rememberEnable = !rememberEnable
            }
            Text("SingleValue Type 설정")
            ChipFlowRow(
                type,
                limit = 1,
                true
            ) {

                when (it) {
                    SingleValueDataType.string.name -> rememberType = SingleValueDataType.string
                    SingleValueDataType.int.name -> rememberType = SingleValueDataType.int
                    SingleValueDataType.long.name -> rememberType = SingleValueDataType.long
                    SingleValueDataType.timestamp.name -> rememberType =
                        SingleValueDataType.timestamp
                }
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Text("SingleValue 입력 예제")
            LimitedTypedTextField(
                5,
                10,
                rememberType
            ) {
                Toast.makeText(
                    this@PropertyUiTestActivity,
                    "입력 데이터 : $it",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}