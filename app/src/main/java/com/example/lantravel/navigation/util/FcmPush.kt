package com.example.lantravel.navigation.util

import android.util.Log
import com.example.lantravel.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class FcmPush {

    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey =
        "AAAAVDbfAhY:APA91bGyKdkMpul6ZQsNWRX4iaIDMbWYgyPyZqu2hT9yhz7FENs3wrxzuu_a-a-B8Lp-yXKIDGeQwEw_Lc0Hkpb6RGO6CAJSo_SAz8us41XBjMIzSmaxdej4918YTa3xhVsuwaeKvnGt"
    var gson: Gson? = null
    var okHttpClient: OkHttpClient? = null

    companion object {
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    var token = task?.result?.get("pushToken").toString()

                    var pushDTO = PushDTO()
                    pushDTO.to = token
                    pushDTO.notification.title = title
                    pushDTO.notification.body = message


                    var body = gson?.toJson(pushDTO)!!.toRequestBody(JSON)
                    var request = Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=" + serverKey)
                        .url(url)
                        .post(body)
                        .build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response.body?.string())
                        }
                    })
                }
            }
    }
}