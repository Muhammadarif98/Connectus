package com.example.connectus.notifications.network

import com.example.connectus.notifications.Constants.Companion.CONTENT_TYPE
import com.example.connectus.notifications.Constants.Companion.SERVER_KEY
import com.example.connectus.notifications.entity.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationAPI {
    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}