package com.codewithleposo.conferencingappdemo.model

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("room_id")
    val room_id: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("role")
    val role: String = "guest",
)