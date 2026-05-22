package com.example.app.data.model

import com.google.gson.annotations.SerializedName

data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val rooms: List<Room>? = null,
    @SerializedName("image_url") val imageUrl: String
)

data class Room(
    val id: String,
    @SerializedName("room_type") val roomType: String,
    val price: Double,
    val images: List<String>? = null
)
