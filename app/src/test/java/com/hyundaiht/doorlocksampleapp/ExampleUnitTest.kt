package com.hyundaiht.doorlocksampleapp

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        checkType<UInt>(8.toUInt())
    }

    fun <T> checkType(obj: T) {
        when(obj){
            is Unit -> println("Unit 타입으로 확인됨!")
            is Any -> println("Any 타입으로 확인됨!")
            is T -> println("T 타입으로 확인됨!")
            else -> println("그 밖에 타입으로 확인됨!")
        }
    }
}