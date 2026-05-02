package com.example.app.data.repository

import com.example.app.data.local.TripDao
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository
import kotlinx.coroutines.flow.Flow

class TripRepositoryImpl(private val tripDao: TripDao) : TripRepository {

    override fun getTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips()
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
}
