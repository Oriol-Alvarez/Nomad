package com.example.app.data.repository

import com.example.app.data.remote.api.HotelApiService
import com.example.app.data.remote.dto.ReserveRequestDto
import com.example.app.data.remote.mapper.*
import com.example.app.domain.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApiService
) : HotelRepository {

    override suspend fun getHotels(groupId: String): List<Hotel> {
        return api.getHotels(groupId).map { it.toDomain() }
    }

    override suspend fun getAvailability(
        groupId: String,
        startDate: String,
        endDate: String,
        hotelId: String?,
        city: String?
    ): List<Hotel> {
        val response = api.getAvailability(groupId, startDate, endDate, hotelId, city)
        return response.availableHotels?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun reserveRoom(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Reservation {
        val request = ReserveRequestDto(hotelId, roomId, startDate, endDate, guestName, guestEmail)
        val response = api.reserveRoom(groupId, request)
        return response.reservation.toDomain()
    }

    override suspend fun cancelReservation(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): ApiMessage {
        val request = ReserveRequestDto(hotelId, roomId, startDate, endDate, guestName, guestEmail)
        val response = api.cancelReservation(groupId, request)
        return response.toDomain()
    }

    override suspend fun getReservations(groupId: String, guestEmail: String?): List<Reservation> {
        val response = api.getReservations(groupId, guestEmail)
        return response.reservations.map { it.toDomain() }
    }

    override suspend fun getReservationById(resId: String): Reservation {
        return api.getReservationById(resId).toDomain()
    }

    override suspend fun cancelReservationById(resId: String): ApiMessage {
        return api.cancelReservationById(resId).toDomain()
    }
}
