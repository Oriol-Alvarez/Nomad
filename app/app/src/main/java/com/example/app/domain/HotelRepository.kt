package com.example.app.domain

interface HotelRepository {
    suspend fun getHotels(groupId: String): List<Hotel>
    
    suspend fun getAvailability(
        groupId: String,
        startDate: String,
        endDate: String,
        hotelId: String? = null,
        city: String? = null
    ): List<Hotel>
    
    suspend fun reserveRoom(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Reservation
    
    suspend fun cancelReservation(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): ApiMessage
    
    suspend fun getReservations(
        groupId: String,
        guestEmail: String? = null
    ): List<Reservation>
    
    suspend fun getReservationById(resId: String): Reservation
    
    suspend fun cancelReservationById(resId: String): ApiMessage
}
