package com.example.app.domain

import com.example.app.data.model.Hotel
import com.example.app.data.model.Reservation
import com.example.app.data.model.ReserveRequest
import com.example.app.data.model.ReserveResponse

interface HotelRepository {
    suspend fun getHotels(groupId: String): Result<List<Hotel>>
    
    suspend fun checkAvailability(
        groupId: String,
        startDate: String,
        endDate: String,
        hotelId: String? = null,
        city: String? = null
    ): Result<List<Hotel>>

    suspend fun reserveRoom(
        groupId: String,
        request: ReserveRequest
    ): Result<ReserveResponse>

    suspend fun cancelReservation(
        resId: String
    ): Result<Reservation>
}
