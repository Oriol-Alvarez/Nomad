package com.example.app.data.remote

import com.example.app.data.model.*
import retrofit2.http.*

interface HotelApiService {

    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String
    ): List<Hotel>

    @GET("hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hotel_id") hotelId: String? = null,
        @Query("city") city: String? = null
    ): AvailabilityResponse

    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): ReserveResponse

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservationPost(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Unit

    @GET("hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): ReservationsResponse

    @DELETE("reservations/{res_id}")
    suspend fun cancelReservation(
        @Path("res_id") resId: String
    ): Reservation
}
