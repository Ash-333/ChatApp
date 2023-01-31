package com.example.localeye.services

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.*
import java.io.IOException

class PushNotificationHelper {

    companion object {
        private val TAG = PushNotificationHelper::class.java.simpleName

        fun sendPushNotification(recipientId: String, senderId: String, senderName: String, messageText: String) {
            // Get the recipient's FCM token
            val recipientTokenRef = FirebaseDatabase.getInstance().reference
                .child("user")
                .child(recipientId)
                .child("token")

            recipientTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val recipientToken = dataSnapshot.getValue(String::class.java)

                    // Prepare the data payload for the FCM message
                    val data = mapOf(
                        "sender_id" to senderId,
                        "sender_name" to senderName,
                        "message" to messageText
                    )

                    // Prepare the request body for the FCM API
                    val requestBody = FCMMessage(
                        to = recipientToken!!,
                        data = data
                    )

                    // Send the push notification using the FCM API
                    val serverKey="AAAAH0ickWk:APA91bHTEeQNBD7fjeX5CaqRSfFkd5aHJKXndIXdudXN2iKOgKtnvuXwySkmbzF2Cpd9YbF6nLDVXwBVe-sjIrhrR1j4VPNxIQ8iIvF2qcsJCDdasV_HZFOravpDMOwiXWSOdHdzkras"
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .header("Authorization", "key=${serverKey}")
                        .header("Content-Type", "application/json")
                        .post(requestBody.toRequestBody())
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e(TAG, "Failed to send FCM message", e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.d(TAG, "FCM message sent")
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to get recipient FCM token", error.toException())
                }
            })
        }
    }
}
