package com.example.app.domain

import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTripsForUser(userId: String): Flow<List<Trip>>
    suspend fun getTripById(id: String): Trip?
    suspend fun insertTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
    suspend fun updateTrip(trip: Trip)
}
