package com.example.app.data.model

import com.google.gson.annotations.SerializedName

data class Reservation(
    val id: String,
    @SerializedName("hotel_id") val hotelId: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("guest_email") val guestEmail: String,
    val hotel: Hotel? = null,
    val room: Room? = null
)

data class ReservationsResponse(
    val reservations: List<Reservation>
)

data class ReserveResponse(
    val message: String,
    val nights: Int,
    val reservation: Reservation
)
