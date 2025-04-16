package com.hyundaiht.doorlocksampleapp.property

import com.google.gson.JsonArray
import com.google.gson.JsonObject


/**
 * TODO
 *
 * @property result
 * @property message
 * @property resultData
 */
data class DeviceResponse(
    val result: Int,
    val message: String,
    val resultData: List<Device>
)

/**
 * TODO
 *
 * @property result
 * @property message
 * @property resultData
 */
data class DevicePropertyResponse(
    val result: Int,
    val message: String,
    val resultData: JsonObject
)

/**
 * TODO
 *
 * @property deviceId
 * @property parentDeviceId
 * @property ownerUserKey
 * @property deviceCategory
 * @property deviceType
 * @property deviceNickname
 * @property deviceUsageRange
 * @property tag
 * @property userInfo
 * @property property
 * @property additionalInfo
 * @property states
 */
data class DeviceDetails(
    val deviceId: String? = null,
    val parentDeviceId: String? = null,
    val ownerUserKey: String? = null,
    val deviceCategory: String? = null,
    val deviceType: String? = null,
    val deviceNickname: String? = null,
    val deviceUsageRange: String? = null,
    val tag: JsonArray? = null,
    val userInfo: JsonObject? = null,
    val properties: DeviceProperty,
    val additionalInfo: JsonObject? = null,
    val states: JsonObject? = null,
)

/**
 * TODO
 *
 * @property deviceId
 * @property ownerUserKey
 * @property parentDeviceId
 * @property deviceCategory
 * @property deviceType
 * @property deviceNickname
 * @property deviceUsageRange
 * @property tag
 * @property userInfo
 */
data class Device(
    val deviceId: String,
    val ownerUserKey: String,
    val parentDeviceId: String? = null,
    val deviceCategory: String,
    val deviceType: String,
    val deviceNickname: String? = null,
    val deviceUsageRange: String,
    val tag: String? = null,
    val userInfo: UserInfo? = null,
)

/**
 * TODO
 *
 * @property userId
 * @property nickname
 */
data class UserInfo(
    val userId: String,
    val nickname: String,
)