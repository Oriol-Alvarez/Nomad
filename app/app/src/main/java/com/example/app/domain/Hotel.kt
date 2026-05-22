package com.example.app.domain

data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val imageUrl: String,
    val rooms: List<Room> = emptyList()
)

data class Room(
    val id: String,
    val roomType: String,
    val price: Double,
    val images: List<String> = emptyList()
)

data class Reservation(
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,
    val hotel: HotelShort? = null,
    val room: RoomShort? = null
)

data class HotelShort(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val imageUrl: String
)

data class RoomShort(
    val id: String,
    val roomType: String,
    val price: Double
)

data class AvailabilityResponse(
    val availableHotels: List<Hotel>
)

data class ApiMessage(
    val message: String
)
