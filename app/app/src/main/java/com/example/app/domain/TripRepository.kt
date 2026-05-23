package com.example.app.domain

import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTripsForUser(userId: String): Flow<List<Trip>>
    suspend fun getTripById(id: String): Trip?
    suspend fun insertTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
    suspend fun updateTrip(trip: Trip)

    // T3: Métodos de imágenes de viaje
    fun getImagesForTrip(tripId: String): Flow<List<TripImage>>
    suspend fun insertTripImage(image: TripImage)
    suspend fun deleteTripImage(id: String)
    suspend fun deleteTripImagesByTripId(tripId: String)
}
