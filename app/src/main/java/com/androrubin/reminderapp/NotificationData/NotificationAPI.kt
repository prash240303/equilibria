package com.androrubin.reminderapp.NotificationData

import com.androrubin.reminderapp.NotificationData.Constants.Companion.CONTENT_TYPE
import com.androrubin.reminderapp.NotificationData.Constants.Companion.SERVER_KEY
import retrofit2.Response
import retrofit2.http.Body
import okhttp3.ResponseBody
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationAPI{

    @Headers("Authorization:key=$SERVER_KEY","Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification:PushNotification
    ): Response<ResponseBody>
}