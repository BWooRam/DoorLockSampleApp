package com.hyundaiht.doorlocksampleapp.property

import RuntimeTypeAdapterFactory
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test


class RuntimeTypeAdapterFactoryTest {
    private var factory: RuntimeTypeAdapterFactory<Configuration>? = null
    private val gson = Gson()

    @Before
    fun setUp() {
        factory = RuntimeTypeAdapterFactory.of(Configuration::class.java, "type")
    }

    @Test
    fun registerSubtype() {
        factory?.registerSubtype(Configuration.OneOfArray::class.java, "")
    }

    @Test
    fun create() {
//        factory?.create(gson, ty)
    }

}