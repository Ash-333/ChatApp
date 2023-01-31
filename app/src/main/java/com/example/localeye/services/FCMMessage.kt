package com.example.localeye.services

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

data class FCMMessage(
    val to: String,
    val data: Map<String, String>
) {
    fun toRequestBody(): RequestBody {
        return RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            Gson().toJson(this)
        )
    }
}