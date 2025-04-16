package com.hyundaiht.doorlocksampleapp.api

import com.hyundaiht.doorlocksampleapp.property.DevicePropertyResponse
import com.hyundaiht.doorlocksampleapp.property.DeviceResponse
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HtHomeAfeApi {

    @GET("devices")
    suspend fun getDeviceList(): Response<DeviceResponse>

    @GET("devices/{deviceId}")
    suspend fun getDeviceProperty(
        @Path("deviceId") deviceId: String
    ): Response<DevicePropertyResponse>

    @POST("devices/{deviceId}/control")
    suspend fun controlDevice(
        @Path("deviceId") deviceId: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @POST("devices/{deviceId}/subscribe")
    suspend fun subscribeDevice(
        @Path("deviceId") deviceId: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @GET("devices/{deviceId}/subscription")
    suspend fun getSubscribeDevice(
        @Path("deviceId") deviceId: String
    ): Response<ResponseBody>
}

fun HtHomeAfeApi.unsubscribeDevice(
    okHttpClient: OkHttpClient,
    deviceId: String,
    jsonBody: String
): Call {
    val request = Request.Builder()
        .url("${ApiModule.HT_IOT_APE_URL2}/devices/${deviceId}/subscribe")
        .delete(jsonBody.toRequestBody("application/json".toMediaTypeOrNull()))  // DELETE 요청에 RequestBody 추가
        .build()

    return okHttpClient.newCall(request)
}