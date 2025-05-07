package com.hyundaiht.doorlocksampleapp.property

import org.junit.Assert.assertEquals
import org.junit.Test


class DeviceTest {
    @Test
    fun addition_isCorrect() {
        val device1 = Device(
            deviceId = "",
            ownerUserKey = "",
            parentDeviceId = "",
            deviceCategory = "",
            deviceType = "",
            deviceNickname = "",
            deviceUsageRange = "",
            tag = "",
            userInfo = null
        )

        val device2 = Device(
            deviceId = "",
            ownerUserKey = "",
            parentDeviceId = "",
            deviceCategory = "",
            deviceType = "",
            deviceNickname = "",
            deviceUsageRange = "",
            tag = "",
            userInfo = null
        )
        assertEquals(device1, device2)
    }
}