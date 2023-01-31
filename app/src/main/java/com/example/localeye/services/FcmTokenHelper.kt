package com.example.localeye.services

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class FcmTokenHelper {
    companion object {
        private val TAG = FcmTokenHelper::class.java.simpleName

        fun subscribeToFcm(context: Context) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@addOnCompleteListener
                    }
                    if (task.isSuccessful) {
                        if (task.result != null && !TextUtils.isEmpty(task.result)) {
                            val token: String = task.result!!
                        }
                    }
                }
        }
    }
}

