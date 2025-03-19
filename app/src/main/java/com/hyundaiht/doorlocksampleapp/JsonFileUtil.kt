package com.hyundaiht.doorlocksampleapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader

object JsonFileUtil {
    fun loadJsonFromAssets(context: Context, fileName: String): String? {
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

    inline fun <reified T> parseJsonFileToList(
        context: Context, fileName: String, gson: Gson = Gson()
    ): List<T> {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
            jsonStringToListClass(reader, gson)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    }

    inline fun <reified T> parseJsonStringToList(
        jsonString: String, gson: Gson = Gson()
    ): List<T> {
        return try {
            val reader = JsonReader(StringReader(jsonString))
            jsonStringToListClass(reader, gson)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    inline fun <reified T> jsonStringToListClass(
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

    inline fun <reified T> parseJsonString(
        jsonString: String, gson: Gson = Gson()
    ): T? {
        return try {
            val reader = JsonReader(StringReader(jsonString))
            gson.fromJson(reader, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}