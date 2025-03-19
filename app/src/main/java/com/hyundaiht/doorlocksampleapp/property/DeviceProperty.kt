package com.hyundaiht.doorlocksampleapp.property

import com.google.gson.JsonObject

/**
 * DeviceProperty
 *
 * @property id
 * @property modelName
 * @property manufacture
 * @property uid
 * @property text
 * @property deviceCategory
 * @property deviceType
 * @property version
 * @property states
 * @property notifications
 */
data class DeviceProperty(
    val id: String,
    val modelName: String,
    val manufacture: String,
    val uid: String,
    val text: String,
    val deviceCategory: String,
    val deviceType: String,
    val version: String? = null,
    val states: List<States> = emptyList(),
    val notifications: List<Notification> = emptyList()
)

/**
 * States
 *
 * @property id
 * @property text
 * @property description
 * @property configuration
 */
data class States(
    val id: String,
    val text: String,
    val description: String,
    val configuration: Configuration,
)

/**
 * Configuration
 *  - OneOfArray
 *  - MultiOfArray
 *  - OneOfRange
 *  - SingleValue
 *  - RecordSet
 */
sealed class Configuration {
    data class OneOfArray(
        val type: String,
        val array: OneOfArrayType,
    ) : Configuration()

    data class MultiOfArray(
        val type: String,
        val limit: Int? = null,
        val array: MultiOfArrayType,
    ) : Configuration()

    data class OneOfRange(
        val type: String,
        val range: OneOfRangeType,
    ) : Configuration()

    data class SingleValue(
        val type: String,
        val value: SingleValueType,
    ) : Configuration()

    data class RecordSet(
        val type: String,
        val limit: Int? = null,
        val record: List<RecordSetType>,
    ) : Configuration()

    /**
     * OneOfArrayType
     *
     * @property dataType
     * - Type : String
     * - Mandatory / Optional : M
     * - Description : string, int
     * @property readable
     * - Type : Array<String>
     * - Mandatory / Optional : M
     * - Description : 읽기 속성값 배열, 빈 배열인 경우 쓰기 전용 속성임을 의미
     * @property writable
     * - Type : Array<String>
     * - Mandatory / Optional : M
     * - Description : 쓰기 속성값 배열, 빈 배열인 경우 읽기 전용 속성임을 의미
     */
    data class OneOfArrayType(
        val dataType: String,
        val readable: List<String> = emptyList(),
        val writable: List<String> = emptyList(),
    )

    /**
     * MultiOfArrayType
     *
     * @property dataType
     * - Type : String
     * - Mandatory / Optional : M
     * - Description : string, int
     * @property readable
     * - Type : Array<String>
     * - Mandatory / Optional : M
     * - Description : 읽기 속성값 배열, 빈 배열인 경우 쓰기 전용 속성임을 의미
     * @property writable
     * - Type : Array<String>
     * - Mandatory / Optional : M
     * - Description : 쓰기 속성값 배열, 빈 배열인 경우 읽기 전용 속성임을 의미
     */
    data class MultiOfArrayType(
        val dataType: String,
        val readable: List<String> = emptyList(),
        val writable: List<String> = emptyList(),
    )

    /**
     * OneOfRangeType
     * - configuration의 type이 oneOfRange인 경우 mandatory
     *
     * @property dataType
     * - Type : String
     * - Mandatory / Optional : M
     * - Description : int
     * @property min
     * - Type : Integer
     * - Mandatory / Optional : M
     * - Description : 속성 최소값
     * @property max
     * - Type : Integer
     * - Mandatory / Optional : M
     * - Description : 속성 최대값
     * @property step
     * - Type : Integer
     * - Mandatory / Optional : M
     * - Description : 속성값  단계
     * @property unit
     * - Type : Integer
     * - Mandatory / Optional : O
     * - Description : 속성값 단위
     * @property editable
     * - Type : Integer
     * - Mandatory / Optional : M
     * - Description : true / false, false의 값을 가지는 경우 제어 불가함을 의미
     */
    data class OneOfRangeType(
        val dataType: String,
        val min: Int,
        val max: Int,
        val step: Int,
        val unit: String?,
        val editable: Boolean,
    )

    /**
     * SingleValueType
     * - configuration의 type이 singleValue인 경우
     *
     * @property dataType
     * - Type : String
     * - Mandatory / Optional : M
     * - Description :string, int, long, timestamp, timestamp의 경우 epoch second 단위의 long 데이터를 의미한다. ex) 1739498854
     * @property min
     * - Type : Integer
     * - Mandatory / Optional : O
     * - Description : 속성 값 길이의 최소값
     * @property max
     * - Type : Integer
     * - Mandatory / Optional : O
     * - Description : 속성 값 길이의 최대값
     * @property digitOnly
     * - Type : Boolean
     * - Mandatory / Optional : MO
     * - Description : dataType이 String인 경우 mandatory, 온전히 숫자로만 구성된 문자열인지 의미
     * @property editable
     * - Type : Boolean
     * - Mandatory / Optional : M
     * - Description : true / false, false의 값을 가지는 경우 변경(제어) 불가능함을 의미
     */
    data class SingleValueType(
        val dataType: String,
        val min: Int? = null,
        val max: Int? = null,
        val digitOnly: Boolean? = null,
        val editable: Boolean,
    )

    /**
     * TODO
     *
     * @property id
     * @property text
     * @property limit
     * @property recordItemConfiguration
     */
    data class RecordSetType(
        val id: String,
        val text: String,
        val limit: Int? = null,
        val recordItemConfiguration: Record
    )

    sealed class Record {
        data class OneOfArray(
            val type: String,
            val dataType: String,
            val readable: List<String> = emptyList(),
            val writable: List<String> = emptyList(),
        ) : Record()

        data class MultiOfArray(
            val type: String,
            val dataType: String,
            val readable: List<String> = emptyList(),
            val writable: List<String> = emptyList()
        ) : Record()

        data class OneOfRange(
            val type: String,
            val dataType: String,
            val min: Int,
            val max: Int,
            val step: Int,
            val unit: String,
            val editable: Boolean
        ) : Record()

        data class SingleValue(
            val type: String,
            val dataType: String,
            val min: Int? = null,
            val max: Int? = null,
            val digitOnly: Boolean?,
            val editable: Boolean
        ) : Record()
    }
}

data class Notification(
    val id: String,
    val argument: List<String>? = null,
    val lang: JsonObject?,
)

/**
 * TODO
 *
 * @return
 */
fun Configuration.Record.MultiOfArray.toConfiguration(limit: Int?): Configuration.MultiOfArray? {
    return kotlin.runCatching {
        Configuration.MultiOfArray(
            type = this@toConfiguration.type,
            limit = limit,
            array = Configuration.MultiOfArrayType(
                dataType = this@toConfiguration.dataType,
                readable = this@toConfiguration.readable,
                writable = this@toConfiguration.writable,
            )
        )
    }.getOrNull()
}

/**
 * TODO
 *
 * @return
 */
fun Configuration.Record.SingleValue.toConfiguration(): Configuration.SingleValue? {
    return kotlin.runCatching {
        Configuration.SingleValue(
            type = this@toConfiguration.type,
            value = Configuration.SingleValueType(
                dataType = this@toConfiguration.dataType,
                min = this@toConfiguration.min,
                max = this@toConfiguration.max,
                digitOnly = this@toConfiguration.digitOnly,
                editable = this@toConfiguration.editable,
            )
        )
    }.getOrNull()
}

/**
 * TODO
 *
 * @return
 */
fun Configuration.Record.OneOfRange.toConfiguration(): Configuration.OneOfRange? {
    return kotlin.runCatching {
        Configuration.OneOfRange(
            type = this@toConfiguration.type,
            range = Configuration.OneOfRangeType(
                dataType = this@toConfiguration.dataType,
                min = this@toConfiguration.min,
                max = this@toConfiguration.max,
                step = this@toConfiguration.step,
                unit = this@toConfiguration.unit,
                editable = this@toConfiguration.editable,
            )
        )
    }.getOrNull()
}

/**
 * TODO
 *
 * @return
 */
fun Configuration.Record.OneOfArray.toConfiguration(): Configuration.OneOfArray? {
    return kotlin.runCatching {
        Configuration.OneOfArray(
            type = this@toConfiguration.type,
            array = Configuration.OneOfArrayType(
                dataType = this@toConfiguration.dataType,
                writable = this@toConfiguration.writable,
                readable = this@toConfiguration.readable,
            )
        )
    }.getOrNull()
}