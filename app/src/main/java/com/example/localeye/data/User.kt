package com.example.localeye.data

import com.example.localeye.services.FcmTokenHelper

data class User(
    val uid:String="",
    val name:String="",
    val email:String="",
    val password:String="",
    val imageUri:String="",
    val status:String="",
    val token:String=""

)
