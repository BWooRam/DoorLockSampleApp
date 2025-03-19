package com.hyundaiht.doorlocksampleapp.property

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenus(items: List<String>, isEnable: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(items.firstOrNull() ?: "") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                if (isEnable)
                    BorderStroke(3.dp, Color.Black)
                else
                    BorderStroke(3.dp, Color.LightGray)
            ),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            Text(
                modifier = Modifier.menuAnchor(),
                text = "UI State : ${if (isEnable) "enable" else "disable"}\nSelect Item : $selectedItem",
                color = if (isEnable) Color.Black else Color.LightGray
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(text = {
                        Text(
                            text = item, color = if (isEnable) Color.Black else Color.LightGray
                        )
                    }, onClick = {
                        if (isEnable) {
                            selectedItem = item
                            expanded = false
                        }
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlowRow(
    items: List<String>,
    limit: Int = 1,
    isEnable: Boolean = true,
    onClick: ((String) -> Unit)? = null
) {
    val selectedOptions = remember {
        mutableStateMapOf<String, Boolean>().apply {
            items.forEach { this[it] = false }
        }
    }
    val selectedCount = selectedOptions.values.count { it }

    FlowRow(
        modifier = Modifier
            .defaultMinSize(minHeight = 40.dp)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //태그 생성
        items.forEach { item ->
            TextButton(
                onClick = {
                    val isSelectable = selectedOptions[item] == true || selectedCount < limit
                    if (isEnable && isSelectable) {
                        selectedOptions[item] = !(selectedOptions[item] ?: false)
                        onClick?.invoke(item)
                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        if (isEnable) {
                            val isSelected = selectedOptions[item] == true
                            if (isSelected) Color.Cyan else Color.Gray
                        } else {
                            Color.LightGray
                        }
                    )
                    .semantics {
                        contentDescription = "ChipOnClickButton"
                    },
                enabled = isEnable
            ) {
                Text(
                    modifier = Modifier.semantics { contentDescription = "ChipText" },
                    text = item,
                    color = if (isEnable) {
                        val isSelected = selectedOptions[item] == true
                        if (isSelected) Color.Blue else Color.Black
                    } else {
                        Color.Gray
                    }
                )
            }
        }
    }
}

@Composable
fun BasicSlider(
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    unit: String,
    isEnable: Boolean = true
) {
    var sliderPosition by remember { mutableFloatStateOf(min) }
    val steps = getUiSteps(min, max, step)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Value: ${sliderPosition.toInt()}, Unit : $unit")

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = min..max,
            steps = steps,
            enabled = isEnable
        )
    }
}

@Composable
fun BoundedCounterUI(
    min: Float = 0f,
    max: Float = 10f,
    step: Float = 1f,
    unit: String,
    isEnable: Boolean = true
) {
    var selectedValue by remember { mutableFloatStateOf(min) }

    Row(
        modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { if (selectedValue > min) selectedValue -= step },
            enabled = isEnable && selectedValue > min
        ) {
            Text("-")
        }

        Text(
            text = "${selectedValue}${unit}",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = if (isEnable) Color.Black else Color.LightGray
        )

        Button(
            onClick = { if (selectedValue < max) selectedValue += step },
            enabled = isEnable && selectedValue < max
        ) {
            Text("+")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioButtons(items: List<String>, isEnable: Boolean = true) {
    var selectedOption by remember { mutableStateOf(items.firstOrNull()) }

    FlowRow {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        if (isEnable) selectedOption = item
                    } // 클릭 시 변경
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (item == selectedOption),
                    onClick = {
                        if (isEnable) selectedOption = item
                    },
                    enabled = isEnable
                )
                Text(
                    text = item,
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (isEnable) Color.Black else Color.LightGray
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasicCheckBoxes(list: List<String>, limit: Int, isEnable: Boolean = true) {
    val selectedOptions = remember {
        mutableStateMapOf<String, Boolean>().apply {
            list.forEach { this[it] = false }
        }
    }
    val selectedCount = selectedOptions.values.count { it }

    FlowRow {
        list.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        val isSelectable = selectedOptions[item] == true || selectedCount < limit
                        if (isEnable && isSelectable)
                            selectedOptions[item] = !(selectedOptions[item] ?: false)
                    }
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = selectedOptions[item] ?: false,
                    onCheckedChange = {
                        val isSelectable = selectedOptions[item] == true || selectedCount < limit
                        if (isEnable && isSelectable)
                            selectedOptions[item] = it
                    },
                    enabled = isEnable
                )
                Text(
                    text = item,
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (isEnable) Color.Black else Color.Gray
                )
            }
        }
    }
}

enum class SingleValueDataType {
    string, int, long, timestamp;

    companion object {
        fun convertString(type: String): SingleValueDataType = when (type) {
            SingleValueDataType.string.name -> SingleValueDataType.string
            SingleValueDataType.int.name -> SingleValueDataType.int
            SingleValueDataType.long.name -> SingleValueDataType.long
            SingleValueDataType.timestamp.name -> SingleValueDataType.timestamp
            else -> throw NullPointerException()
        }
    }
}

@Composable
fun LimitedTypedTextField(
    minLength: Int,
    maxLength: Int,
    allowedType: SingleValueDataType,
    isEnable: Boolean = true,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { input ->
                    val isValidType = when (allowedType) {
                        SingleValueDataType.string -> true // 문자열은 그대로 허용
                        SingleValueDataType.int -> input.all { it.isDigit() } // 숫자만 허용
                        SingleValueDataType.long -> input.all { it.isDigit() } // 숫자만 허용
                        SingleValueDataType.timestamp -> input.all { it.isDigit() } && input.length <= 10 // 10자리만 허용
                    }

                    if (isValidType && input.length <= maxLength) {
                        text = input
                        isError = false
                    } else {
                        isError = true
                        errorMessage = "Invalid input for $allowedType"
                    }
                },
                label = { Text("Enter $allowedType") },
                isError = isError,
                keyboardOptions = when (allowedType) {
                    SingleValueDataType.int,
                    SingleValueDataType.long,
                    SingleValueDataType.timestamp -> KeyboardOptions(keyboardType = KeyboardType.Number)

                    else -> KeyboardOptions.Default
                },
                singleLine = true,
                modifier = Modifier.weight(1f),
                enabled = isEnable
            )

            // 지우기 버튼 (Clear 버튼)
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    if (isEnable) {
                        text = ""
                        isError = false
                    }
                }, enabled = isEnable) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Text")
                }
            }
        }

        Text(
            text = "Length: ${text.length} / $maxLength",
            modifier = Modifier.padding(top = 4.dp),
            color = if (isError) Color.Red else Color.Gray
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                if (isEnable && text.length >= minLength) {
                    onConfirm(text) // 입력이 유효하면 이벤트 발생
                } else {
                    isError = true
                    errorMessage = "Minimum length required: $minLength"
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = isEnable
        ) {
            Text("Confirm")
        }
    }
}


fun getUiSteps(min: Float, max: Float, step: Float) = ((max - min) / step).toInt() - 1