package com.example.app.data.repository

import com.example.app.data.model.Hotel
import com.example.app.data.model.Reservation
import com.example.app.data.model.ReserveRequest
import com.example.app.data.model.ReserveResponse
import com.example.app.data.remote.HotelApiService
import com.example.app.domain.HotelRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val apiService: HotelApiService
) : HotelRepository {

    override suspend fun getHotels(groupId: String): Result<List<Hotel>> = runCatching {
        apiService.getHotels(groupId)
    }

    override suspend fun checkAvailability(
        groupId: String,
        startDate: String,
        endDate: String,
        hotelId: String?,
        city: String?
    ): Result<List<Hotel>> = runCatching {
        apiService.checkAvailability(
            groupId = groupId,
            startDate = startDate,
            endDate = endDate,
            hotelId = hotelId,
            city = city
        ).availableHotels
    }

    override suspend fun reserveRoom(
        groupId: String,
        request: ReserveRequest
    ): Result<ReserveResponse> = runCatching {
        apiService.reserveRoom(groupId, request)
    }

    override suspend fun cancelReservation(
        resId: String
    ): Result<Reservation> = runCatching {
        apiService.cancelReservation(resId)
    }
}
