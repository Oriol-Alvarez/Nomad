package com.example.app.data.repository

import com.example.app.data.local.TripDao
import com.example.app.domain.Trip
import com.example.app.domain.TripImage
import com.example.app.domain.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    override fun getTripsForUser(userId: String): Flow<List<Trip>> {
        return tripDao.getTripsForUser(userId)
    }

    override suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    override suspend fun getTripById(id: String): Trip? {
        return tripDao.getTripById(id)
    }

    override suspend fun deleteTrip(id: String) {
        tripDao.deleteTripById(id)
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    // T3: Métodos de imágenes de viaje
    override fun getImagesForTrip(tripId: String): Flow<List<TripImage>> {
        return tripDao.getImagesForTrip(tripId)
    }

    override suspend fun insertTripImage(image: TripImage) {
        tripDao.insertTripImage(image)
    }

    override suspend fun deleteTripImage(id: String) {
        tripDao.deleteTripImageById(id)
    }

    override suspend fun deleteTripImagesByTripId(tripId: String) {
        tripDao.deleteTripImagesByTripId(tripId)
    }
}
